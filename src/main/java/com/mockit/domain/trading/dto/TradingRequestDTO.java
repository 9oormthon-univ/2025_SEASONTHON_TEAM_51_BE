package com.mockit.domain.trading.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

public class TradingRequestDTO {

    @Getter
    @Setter
    public static class PredictRequestDto {
        private String symbol;
        private int limit;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateOrderDto {
        @NotBlank(message = "심볼은 필수 입력값입니다.")
        private String symbol;

        @NotBlank(message = "주문 타입은 필수 입력값입니다.")
        private String side;

        @NotBlank(message = "주문 종류는 필수 입력값입니다.")
        private String type;

        @NotNull(message = "수량은 필수 입력값입니다.")
        @DecimalMin(value = "0.0", inclusive = false, message = "수량은 0보다 커야 합니다.")
        private BigDecimal qty;

        private BigDecimal price; // 지정가 주문 시 사용
    }
}
