package com.mockit.domain.trading.dto;

import lombok.Getter;
import lombok.Setter;

public class TradingRequestDTO {

    @Getter
    @Setter
    public static class PredictRequestDto {
        private String symbol;
        private int limit;
    }
}
