package com.mockit.domain.trade.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class PositionItemResponse {

    private String symbol;
    private BigDecimal qty;
    private BigDecimal avgPrice;
    private BigDecimal currentPrice;
    private BigDecimal profitAndLoss;

    @Builder
    private PositionItemResponse(String symbol, BigDecimal qty, BigDecimal avgPrice,
                                 BigDecimal currentPrice, BigDecimal profitAndLoss) {
        this.symbol = symbol;
        this.qty = qty;
        this.avgPrice = avgPrice;
        this.currentPrice = currentPrice;
        this.profitAndLoss = profitAndLoss;
    }

    public static PositionItemResponse of(String symbol, BigDecimal qty, BigDecimal avgPrice,
                                          BigDecimal currentPrice) {
        var pnl = currentPrice.subtract(avgPrice).multiply(qty);
        return PositionItemResponse.builder()
                .symbol(symbol).qty(qty).avgPrice(avgPrice)
                .currentPrice(currentPrice).profitAndLoss(pnl)
                .build();
    }
}
