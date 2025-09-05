package com.mockit.domain.market.service;

import com.mockit.domain.market.repository.CandleRepository;
import com.mockit.domain.market.entity.Candle;
import com.mockit.domain.market.exception.MarketErrorStatus;
import com.mockit.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DbPriceProvider implements PriceProvider {

    private final CandleRepository candleRepository;

    @Override
    public BigDecimal lastPrice(String symbol) {
        return lastPrice(symbol, "1d");
    }

    @Override
    public BigDecimal lastPrice(String symbol, String tf) {
        return candleRepository
                .findTopByIdStockCodeAndIdTfOrderByIdTsDesc(symbol, tf)
                .map(Candle::getClosePrice)
                .orElseThrow(() -> new GeneralException(MarketErrorStatus.SYMBOL_NOT_FOUND));
    }
}
