package com.mockit.domain.trading.converter;

import com.mockit.domain.trading.domain.entity.Orders;
import com.mockit.domain.trading.domain.entity.Positions;
import com.mockit.domain.trading.domain.entity.Stocks;
import com.mockit.domain.trading.dto.TradingResponseDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TradingConverter {

    public static TradingResponseDTO.OrderDto toOrderDto(Orders order) {
        return TradingResponseDTO.OrderDto.builder()
                .orderId(order.getId())
                .status(order.getStatus().name())
                .message("주문이 성공적으로 생성되었습니다.")
                .build();
    }

    public TradingResponseDTO.OrderListDto toOrderListDto(Orders order) {
        return TradingResponseDTO.OrderListDto.builder()
                .orderId(order.getId())
                .symbol(order.getStockCode())
                .side(order.getSide().name())
                .qty(order.getQty())
                .status(order.getStatus().name())
                .price(order.getPrice())
                .orderTime(order.getCreatedAt())
                .build();
    }

    public static TradingResponseDTO.PositionDto toPositionDto(Positions position, Stocks stock) {
        BigDecimal currentPrice = stock.getCurrentPrice();
        BigDecimal profitAndLoss = (currentPrice.subtract(position.getAvgPrice())).multiply(position.getQty());

        return TradingResponseDTO.PositionDto.builder()
                .symbol(position.getStockCode())
                .qty(position.getQty())
                .avgPrice(position.getAvgPrice())
                .currentPrice(currentPrice)
                .profitAndLoss(profitAndLoss)
                .build();
    }
}
