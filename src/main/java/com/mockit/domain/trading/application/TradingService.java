package com.mockit.domain.trading.application;

import com.mockit.domain.trading.domain.entity.Orders;
import com.mockit.domain.trading.dto.TradingRequestDTO;
import com.mockit.domain.trading.dto.TradingResponseDTO;

import java.util.List;

public interface TradingService {

    TradingResponseDTO.OrderDto createOrder(Long memberId, TradingRequestDTO.CreateOrderDto request);

    void handleOrderExecution(Orders order);

    List<TradingResponseDTO.OrderListDto> getOrders(Long memberId, String status);

    List<TradingResponseDTO.PositionDto> getPositions(Long memberId);

    TradingResponseDTO.PortfolioDto getPortfolio(Long memberId);

    TradingResponseDTO.CancelOrderDto cancelOrder(Long memberId, Long orderId);

}
