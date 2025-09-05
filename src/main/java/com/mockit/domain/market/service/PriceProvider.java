package com.mockit.domain.market.service;

import java.math.BigDecimal;

public interface PriceProvider {

    BigDecimal lastPrice(String symbol);  // 최신가 (기본: 1d close)
    BigDecimal lastPrice(String symbol, String tf);  // 필요시 분봉
}