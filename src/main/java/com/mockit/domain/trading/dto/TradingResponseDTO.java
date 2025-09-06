package com.mockit.domain.trading.dto;

import lombok.*;

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

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderDto {
        private Long orderId;
        private String status;
        private String message;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderListDto {
        private Long orderId;
        private String symbol;
        private String side;
        private BigDecimal qty;
        private String status;
        private BigDecimal price;
        private LocalDateTime orderTime;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PositionDto {
        private String symbol;
        private BigDecimal qty;
        private BigDecimal avgPrice;
        private BigDecimal currentPrice;
        private BigDecimal profitAndLoss;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PortfolioDto {
        private BigDecimal totalCash;
        private BigDecimal totalAssetValue;
        private BigDecimal totalPortfolioValue;
        private BigDecimal totalProfitAndLoss;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CancelOrderDto {
        private boolean success;
        private String message;
    }
}
