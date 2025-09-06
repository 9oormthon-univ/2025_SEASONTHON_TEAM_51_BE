package com.mockit.domain.trade.service;

import com.mockit.domain.trade.enums.Side;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MatchingEngine {

    public boolean canFillLimit(Side side, BigDecimal limitPrice, BigDecimal lastPrice) {
        if (limitPrice == null || lastPrice == null) {
            return false;
        }

        return switch (side) {
            case BUY  -> lastPrice.compareTo(limitPrice) <= 0;
            case SELL -> lastPrice.compareTo(limitPrice) >= 0;
        };
    }
}
