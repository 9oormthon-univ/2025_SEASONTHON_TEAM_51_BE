package com.mockit.domain.trade.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class PortfolioResponse {

    private BigDecimal totalCash;
    private BigDecimal totalAssetValue;
    private BigDecimal totalPortfolioValue;
    private BigDecimal totalProfitAndLoss;

    @Builder
    private PortfolioResponse(BigDecimal totalCash, BigDecimal totalAssetValue,
                              BigDecimal totalPortfolioValue, BigDecimal totalProfitAndLoss) {
        this.totalCash = totalCash;
        this.totalAssetValue = totalAssetValue;
        this.totalPortfolioValue = totalPortfolioValue;
        this.totalProfitAndLoss = totalProfitAndLoss;
    }

    public static PortfolioResponse of(BigDecimal cash, BigDecimal asset, BigDecimal pnl) {
        return PortfolioResponse.builder()
                .totalCash(cash)
                .totalAssetValue(asset)
                .totalPortfolioValue(cash.add(asset))
                .totalProfitAndLoss(pnl)
                .build();
    }
}
