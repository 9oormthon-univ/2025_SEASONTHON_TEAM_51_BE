package com.mockit.domain.trading.api;

import com.mockit.domain.trading.application.TradingService;
import com.mockit.domain.trading.dto.TradingRequestDTO;
import com.mockit.domain.trading.dto.TradingResponseDTO;
import com.mockit.global.common.response.BaseResponse;
import com.mockit.global.error.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trading")
@RequiredArgsConstructor
public class TradingRestController {

    private final TradingService tradingService;

    // TODO: 현재 로그인된 사용자 ID를 가져오는 로직 필요
    private final Long memberId = 1L;

    @PostMapping("/orders")
    @Operation(summary = "주문 생성 API", description = "새로운 매수/매도 주문을 생성합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "OK", description = "주문이 성공적으로 생성되었습니다.")
    })
    public BaseResponse<TradingResponseDTO.OrderDto> createOrder(
            @RequestBody @Valid TradingRequestDTO.CreateOrderDto request
    ) {
        TradingResponseDTO.OrderDto result = tradingService.createOrder(memberId, request);
        return BaseResponse.onSuccess(SuccessStatus.OK, result);
    }

    @GetMapping("/orders")
    @Operation(summary = "주문 목록 조회 API", description = "사용자의 주문 상태별 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "주문 목록이 성공적으로 조회되었습니다.")
    })
    public BaseResponse<List<TradingResponseDTO.OrderListDto>> getOrders(
            @RequestParam(name = "status") String status
    ) {
        List<TradingResponseDTO.OrderListDto> result = tradingService.getOrders(memberId, status);
        return BaseResponse.onSuccess(SuccessStatus.OK, result);
    }

    @GetMapping("/positions")
    @Operation(summary = "보유 주식 포지션 조회 API", description = "사용자의 현재 보유 주식 포지션 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "보유 주식 포지션 목록이 성공적으로 조회되었습니다.")
    })
    public BaseResponse<List<TradingResponseDTO.PositionDto>> getPositions(
            //@RequestHeader(name = "Authorization") String authorizationHeader
    ) {
        List<TradingResponseDTO.PositionDto> result = tradingService.getPositions(memberId);
        return BaseResponse.onSuccess(SuccessStatus.OK, result);
    }

    @GetMapping("/portfolio")
    @Operation(summary = "총 자산 현황 조회 API", description = "사용자의 총 자산 평가액 및 손익 정보를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "총 자산 현황이 성공적으로 조회되었습니다.")
    })
    public BaseResponse<TradingResponseDTO.PortfolioDto> getPortfolio(
            //@RequestHeader(name = "Authorization") String authorizationHeader
    ) {
        TradingResponseDTO.PortfolioDto result = tradingService.getPortfolio(memberId);
        return BaseResponse.onSuccess(SuccessStatus.OK, result);
    }

    @PostMapping("/orders/{orderId}/cancel")
    @Operation(summary = "주문 취소 API", description = "미체결 주문을 취소합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "주문이 성공적으로 취소되었습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "주문을 찾을 수 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "주문 취소 권한이 없습니다.")
    })
    public BaseResponse<TradingResponseDTO.CancelOrderDto> cancelOrder(
            //@RequestHeader(name = "Authorization") String authorizationHeader,
            @PathVariable Long orderId
    ) {
        TradingResponseDTO.CancelOrderDto result = tradingService.cancelOrder(memberId, orderId);
        return BaseResponse.onSuccess(SuccessStatus.OK, result);
    }

}