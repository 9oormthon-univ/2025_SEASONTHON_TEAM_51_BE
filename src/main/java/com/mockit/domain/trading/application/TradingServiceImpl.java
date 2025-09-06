package com.mockit.domain.trading.application;

import com.mockit.domain.market.entity.Candle;
import com.mockit.domain.market.entity.CandleId;
import com.mockit.domain.market.repository.CandleRepository;
import com.mockit.domain.python.PythonPredictionApiClient;
import com.mockit.domain.trading.converter.TradingConverter;
import com.mockit.domain.trading.domain.entity.Stocks;
import com.mockit.domain.trading.domain.repository.StocksRepository;
import com.mockit.domain.trading.dto.TradingResponseDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TradingServiceImpl implements TradingService {

    private final CandleRepository candlesRepository;
    private final PythonPredictionApiClient predictionApiClient;
    private final TradingConverter tradingConverter;
    private static final int AI_MIN_DATA_POINTS = 5;
    private final StocksRepository stocksRepository;
    private static final Logger log = LoggerFactory.getLogger(TradingServiceImpl.class);

    @Transactional
    public List<TradingResponseDTO.QuoteDto> getQuotes(List<String> symbols) {
        List<TradingResponseDTO.QuoteDto> fetchedQuotes = predictionApiClient.getQuotes(symbols).block();
        fetchedQuotes.forEach(quote -> {
            Stocks stock = stocksRepository.findByStockCode(quote.getSymbol())
                    .orElse(new Stocks());

            stock.setStockCode(quote.getSymbol());
            if (stock.getStockName() == null || stock.getStockName().isEmpty()) {
                stock.setStockName(quote.getSymbol());
            }
            stock.setCurrentPrice(quote.getPrice());
            stocksRepository.save(stock);
        });
        return fetchedQuotes;
    }


    @Transactional
    public List<TradingResponseDTO.CandleDto> getCandles(String symbol, String tf, int limit) {
        // 1. 필요한 총 데이터 개수
        int requiredDataPoints = Math.max(limit, AI_MIN_DATA_POINTS);

        // 2. DB에서 최근 캔들 데이터를 조회합니다.
        List<Candle> recentCandles = getRecentCandles(symbol, tf, requiredDataPoints);

        // 3. AI 예측을 위한 최소 데이터가 부족할 경우 외부 API 호출
        if (recentCandles.size() < AI_MIN_DATA_POINTS) {
            log.info("AI 예측을 위한 최소 데이터({})가 부족합니다. 외부 API에서 과거 데이터를 가져옵니다.", AI_MIN_DATA_POINTS);

            // 데이터 저장 및 반환 로직을 호출합니다.
            List<Candle> allHistoricalCandles = saveAndReturnHistoricalCandles(symbol, tf);

            // 다시 한번 데이터가 충분한지 확인
            if (allHistoricalCandles.size() < AI_MIN_DATA_POINTS) {
                log.warn("AI 예측에 필요한 최소 데이터({})가 여전히 부족합니다. 현재 데이터 수: {}. 예측을 진행할 수 없습니다.", AI_MIN_DATA_POINTS, allHistoricalCandles.size());
                return Collections.emptyList();
            }

            // 예측을 수행하고 결과를 반환합니다.
            return predictAndCombineCandles(allHistoricalCandles, limit);
        }

        // 4. 최신 데이터가 오늘 날짜가 아닐 경우 AI 예측을 수행
        if (recentCandles.get(0).getId().getTs().toLocalDate().isBefore(LocalDateTime.now().toLocalDate())) {
            log.info("최신 데이터가 오늘 날짜가 아닙니다. AI 예측을 진행합니다.");
            return predictAndCombineCandles(recentCandles, limit);
        }

        // 5. 모든 조건이 충족되면 기존 데이터만 반환합니다.
        return recentCandles.stream()
                .map(tradingConverter::toCandleDto)
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Candle> saveAndReturnHistoricalCandles(String symbol, String tf) {
        List<TradingResponseDTO.CandleDto> fetchedCandles = predictionApiClient.getHistoricalCandles(symbol, "1y", tf).block();

        if (fetchedCandles != null && !fetchedCandles.isEmpty()) {
            for (TradingResponseDTO.CandleDto dto : fetchedCandles) {
                // 복합 키 생성
                CandleId candleId = new CandleId(symbol, tf, dto.getTs().toLocalDate().atStartOfDay());

                // 1. 데이터베이스에서 해당 키를 가진 캔들을 찾습니다.
                Optional<Candle> existingCandleOptional = candlesRepository.findById(candleId);

                if (existingCandleOptional.isPresent()) {
                    // 2. 이미 존재하는 경우: 업데이트
                    Candle existingCandle = existingCandleOptional.get();
                    existingCandle.setOpenPrice(dto.getOpen());
                    existingCandle.setHighPrice(dto.getHigh());
                    existingCandle.setLowPrice(dto.getLow());
                    existingCandle.setClosePrice(dto.getClose());
                    existingCandle.setVolume(dto.getVolume());
                    candlesRepository.save(existingCandle); // update
                } else {
                    // 3. 존재하지 않는 경우: 삽입
                    Candle newCandle = tradingConverter.toEntity(dto, symbol, tf);
                    candlesRepository.save(newCandle); // insert
                }
            }
            log.info("성공적으로 {}일치 데이터를 가져와 DB에 저장 또는 업데이트했습니다.", fetchedCandles.size());
        }

        int fetchLimit = fetchedCandles != null ? fetchedCandles.size() : 0;
        return getRecentCandles(symbol, tf, Math.max(AI_MIN_DATA_POINTS, fetchLimit));
    }



    private List<TradingResponseDTO.CandleDto> predictAndCombineCandles(List<Candle> candles, int limit) {
        List<Candle> historicalDataForAI = candles.stream()
                .limit(AI_MIN_DATA_POINTS)
                .collect(Collectors.toList());

        if (historicalDataForAI.size() < AI_MIN_DATA_POINTS) {
            log.warn("AI 예측에 필요한 최소 데이터({})가 부족합니다. 현재 데이터 수: {}. 예측을 진행할 수 없습니다.", AI_MIN_DATA_POINTS, historicalDataForAI.size());
            return Collections.emptyList();
        }

        TradingResponseDTO.PredictResponseDto predictedData = predictionApiClient.predict(historicalDataForAI);

        TradingResponseDTO.CandleDto predictedCandle = tradingConverter.toCandleDto(predictedData, historicalDataForAI);

        List<TradingResponseDTO.CandleDto> candleDtos = candles.stream()
                .map(tradingConverter::toCandleDto)
                .limit(limit)
                .collect(Collectors.toList());
        candleDtos.add(0, predictedCandle);
        return candleDtos;
    }

    private List<Candle> getRecentCandles(String symbol, String tf, int limit) {
        return candlesRepository.findAllByIdStockCodeAndIdTfOrderByIdTsDesc(symbol, tf, PageRequest.of(0, limit));
    }
}
