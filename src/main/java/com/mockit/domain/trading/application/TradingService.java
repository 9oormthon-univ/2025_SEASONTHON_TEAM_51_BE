package com.mockit.domain.trading.application;

import com.mockit.domain.market.entity.Candle;
import com.mockit.domain.trading.dto.TradingResponseDTO;

import java.util.List;

public interface TradingService {

    List<TradingResponseDTO.QuoteDto> getQuotes(List<String> symbols);

    List<TradingResponseDTO.CandleDto> getCandles(String symbol, String tf, int limit);

    List<Candle> saveAndReturnHistoricalCandles(String symbol, String tf);

}
