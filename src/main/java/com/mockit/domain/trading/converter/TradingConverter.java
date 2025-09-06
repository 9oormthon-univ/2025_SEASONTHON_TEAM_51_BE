package com.mockit.domain.trading.converter;

import com.mockit.domain.market.entity.Candle;
import com.mockit.domain.market.entity.CandleId;
import com.mockit.domain.trading.dto.TradingResponseDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class TradingConverter {

    public TradingResponseDTO.CandleDto toCandleDto(Candle entity) {
        if (entity == null) {
            return null;
        }
        TradingResponseDTO.CandleDto dto = new TradingResponseDTO.CandleDto();
        dto.setTs(entity.getId().getTs());
        dto.setOpen(entity.getOpenPrice());
        dto.setHigh(entity.getHighPrice());
        dto.setLow(entity.getLowPrice());
        dto.setClose(entity.getClosePrice());
        dto.setVolume(entity.getVolume());
        return dto;
    }

    // 예측 모델 결과를 CandleDto로 변환하는 메서드 (수정됨)
    public TradingResponseDTO.CandleDto toCandleDto(TradingResponseDTO.PredictResponseDto prediction, List<Candle> historicalData) {
        if (prediction == null || historicalData == null || historicalData.isEmpty()) {
            return null;
        }

        // AI 예측에 사용된 가장 최근의 과거 캔들 데이터를 가져옵니다.
        Candle lastCandle = historicalData.get(0);
        BigDecimal lastClosePrice = lastCandle.getClosePrice();

        // AI 예측된 종가 변화율을 사용하여 예측 종가(predictedClose)를 계산합니다.
        BigDecimal predictedPctChange = new BigDecimal(prediction.getPredictedPctChange());
        BigDecimal predictedClose = lastClosePrice.multiply(BigDecimal.ONE.add(predictedPctChange))
                .setScale(2, RoundingMode.HALF_UP);

        // 나머지 값들을 추정하는 로직 (간단한 예시)
        // 실제로는 더 복잡한 로직을 사용해 정확도를 높일 수 있습니다.
        BigDecimal openPrice = lastCandle.getClosePrice();
        BigDecimal highPrice = predictedClose.max(openPrice);
        BigDecimal lowPrice = predictedClose.min(openPrice);

        TradingResponseDTO.CandleDto dto = new TradingResponseDTO.CandleDto();
        dto.setTs(LocalDateTime.now());
        dto.setOpen(openPrice.setScale(2, RoundingMode.HALF_UP));
        dto.setHigh(highPrice.setScale(2, RoundingMode.HALF_UP));
        dto.setLow(lowPrice.setScale(2, RoundingMode.HALF_UP));
        dto.setClose(predictedClose);
        dto.setVolume(0L); // 거래량은 예측하지 않으므로 0으로 설정

        return dto;
    }

    public Candle toEntity(TradingResponseDTO.CandleDto dto, String symbol, String tf) {
        if (dto == null) {
            return null;
        }
        // 새로운 Candle 엔티티의 생성자에 맞게 수정
        CandleId id = new CandleId(symbol, tf, dto.getTs().toLocalDate().atStartOfDay());
        Candle entity = new Candle(id, dto.getOpen(), dto.getHigh(), dto.getLow(), dto.getClose(), dto.getVolume());
        return entity;
    }
}
