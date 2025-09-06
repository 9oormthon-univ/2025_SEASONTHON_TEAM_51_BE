package com.mockit.domain.trade.dto.response;

import com.mockit.domain.trade.entity.Order;
import com.mockit.domain.trade.enums.OrderStatus;
import com.mockit.domain.trade.enums.OrderType;
import com.mockit.domain.trade.enums.Side;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class OrderListItemResponse {

    private final Long orderId;
    private final String symbol;
    private final Side side;
    private final OrderType type;
    private final BigDecimal price;
    private final BigDecimal qty;
    private final OrderStatus status;
    private final LocalDateTime createdAt;

    @Builder
    private OrderListItemResponse(Long orderId, String symbol, Side side, OrderType type,
                                  BigDecimal price, BigDecimal qty, OrderStatus status, LocalDateTime createdAt) {
        this.orderId = orderId;
        this.symbol = symbol;
        this.side = side;
        this.type = type;
        this.price = price;
        this.qty = qty;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static OrderListItemResponse from(Order o) {
        return OrderListItemResponse.builder()
                .orderId(o.getId())
                .symbol(o.getStockCode())
                .side(o.getSide())
                .type(o.getType())
                .price(o.getPrice())
                .qty(o.getQty())
                .status(o.getStatus())
                .createdAt(o.getCreatedAt())
                .build();
    }
}
