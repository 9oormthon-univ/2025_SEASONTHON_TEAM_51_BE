package com.mockit.domain.python;

import com.mockit.domain.market.entity.Candle;
import com.mockit.domain.trading.dto.TradingResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PythonPredictionApiClient {

    private final WebClient webClient;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public TradingResponseDTO.PredictResponseDto predict(List<Candle> ohlcData) {
        // Python 서버의 API 요청 형식에 맞게 데이터 변환
        List<List<Double>> ohlcList = ohlcData.stream()
                .map(c -> List.of(
                        c.getOpenPrice().doubleValue(),
                        c.getHighPrice().doubleValue(),
                        c.getLowPrice().doubleValue(),
                        c.getClosePrice().doubleValue()
                ))
                .collect(Collectors.toList());

        // 요청 바디를 Map으로 구성
        Map<String, List<List<Double>>> requestBody = Map.of("ohlc_data", ohlcList);

        // WebClient를 사용하여 Python 서버에 POST 요청
        // 응답을 Map으로 받은 후 PredictResponseDto로 변환
        return webClient.post()
                .uri("/predict")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class) // 응답을 Map으로 받음
                .map(responseMap -> { // Map을 PredictResponseDto로 변환
                    TradingResponseDTO.PredictResponseDto dto = new TradingResponseDTO.PredictResponseDto();
                    dto.setPredictedPctChange(((Number) responseMap.get("predicted_pct_change")).doubleValue());
                    dto.setPredictedPriceDirection(((Number) responseMap.get("predicted_price_direction")).intValue());
                    dto.setPredictionProbability(((Number) responseMap.get("prediction_probability")).doubleValue());
                    return dto;
                })
                .block();
    }

    public Mono<List<TradingResponseDTO.QuoteDto>> getQuotes(List<String> symbols) {
        String symbolsParam = String.join(",", symbols);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/quotes")
                        .queryParam("symbols", symbolsParam)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (List<Map<String, ?>>) response.get("quotes"))
                .flatMapIterable(list -> list)
                .map(map -> {
                    TradingResponseDTO.QuoteDto quoteDto = new TradingResponseDTO.QuoteDto();

                    quoteDto.setSymbol((String) map.get("symbol"));

                    Object priceObj = map.get("price");
                    if (priceObj instanceof Number) {
                        quoteDto.setPrice(new BigDecimal(((Number) priceObj).doubleValue()));
                    }

                    Object changeObj = map.get("change");
                    if (changeObj instanceof Number) {
                        quoteDto.setChange(new BigDecimal(((Number) changeObj).doubleValue()));
                    }

                    Object changeRateObj = map.get("change_rate");
                    if (changeRateObj instanceof Number) {
                        quoteDto.setChangeRate(new BigDecimal(((Number) changeRateObj).doubleValue()));
                    }
                    return quoteDto;
                })
                .collectList();
    }

    // 새로운 메서드: 캔들 데이터 조회
    public Mono<List<TradingResponseDTO.CandleDto>> getCandles(String symbol, String period, String interval) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/candles")
                        .queryParam("symbol", symbol)
                        .queryParam("period", period)
                        .queryParam("interval", interval)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (List<Map<String, ?>>) response.get("candles"))
                .flatMapIterable(list -> list)
                .map(map -> {
                    TradingResponseDTO.CandleDto candleDto = new TradingResponseDTO.CandleDto();
                    // Map의 데이터를 CandleDto로 변환하는 로직 구현
                    return candleDto;
                })
                .collectList();
    }

    public Mono<List<TradingResponseDTO.CandleDto>> getHistoricalCandles(String symbol, String period, String tf) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/candles")
                        .queryParam("symbol", symbol)
                        .queryParam("period", period)
                        .queryParam("interval", tf) // tf를 interval 파라미터로 전달
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (List<Map<String, ?>>) response.get("candles"))
                .flatMapIterable(list -> list)
                .map(map -> {
                    TradingResponseDTO.CandleDto candleDto = new TradingResponseDTO.CandleDto();
                    candleDto.setTs(LocalDateTime.parse((String) map.get("ts"), DATE_FORMATTER));
                    candleDto.setOpen(new BigDecimal(((Number) map.get("open")).doubleValue()));
                    candleDto.setHigh(new BigDecimal(((Number) map.get("high")).doubleValue()));
                    candleDto.setLow(new BigDecimal(((Number) map.get("low")).doubleValue()));
                    candleDto.setClose(new BigDecimal(((Number) map.get("close")).doubleValue()));
                    candleDto.setVolume(((Number) map.get("volume")).longValue());
                    return candleDto;
                })
                .collectList();
    }
}
