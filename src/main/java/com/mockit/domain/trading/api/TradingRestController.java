package com.mockit.domain.trading.api;

import com.mockit.domain.trading.application.TradingService;
import com.mockit.domain.trading.dto.TradingResponseDTO;
import com.mockit.global.common.response.BaseResponse;
import com.mockit.global.error.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/market")
@RequiredArgsConstructor
public class TradingRestController {

    private final TradingService tradingService;

    @GetMapping("/quotes")
    @Operation(summary = "주식 현재가 조회 API", description = "특정 주식의 현재가를 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse( responseCode = "HANDOVER_200", description = "OK, 성공적으로 생성되었습니다.")
    })
    public BaseResponse<List<TradingResponseDTO.QuoteDto>> getQuotes(
            @RequestParam String symbols
    ) {
        List<String> symbolList = List.of(symbols.split(","));
        List<TradingResponseDTO.QuoteDto> result = tradingService.getQuotes(symbolList);
        return BaseResponse.onSuccess(SuccessStatus.OK, result);
    }

    @GetMapping("/candles")
    @Operation(summary = "주식 OHLCV 조회 API", description = "특정 주식의 OHLCV를 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse( responseCode = "HANDOVER_200", description = "OK, 성공적으로 생성되었습니다.")
    })
    public BaseResponse<List<TradingResponseDTO.CandleDto>> getCandles(
            @RequestParam String symbol,
            @RequestParam(defaultValue = "1d") String tf,
            @RequestParam(defaultValue = "200") int limit
    ) {
        List<TradingResponseDTO.CandleDto> result = tradingService.getCandles(symbol, tf, limit);
        return BaseResponse.onSuccess(SuccessStatus.OK, result);
    }
}
