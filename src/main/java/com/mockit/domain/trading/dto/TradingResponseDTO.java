package com.mockit.domain.trading.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TradingResponseDTO {

    @Getter
    @Setter
    public static class QuoteDto {
        private String symbol;
        private BigDecimal price;
        private BigDecimal change;
        private BigDecimal changeRate;
    }

    @Getter
    @Setter
    public static class CandleDto {
        private LocalDateTime ts;
        private BigDecimal open;
        private BigDecimal high;
        private BigDecimal low;
        private BigDecimal close;
        private Long volume;
    }

    @Getter
    @Setter
    public static class PredictResponseDto {
        private double predictedPctChange;
        private int predictedPriceDirection;
        private double predictionProbability;
    }
}
