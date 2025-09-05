package com.mockit.domain.trade.dto.response;

import com.mockit.domain.trade.entity.Order;
import com.mockit.domain.trade.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class OrderResponse {

    private Long orderId;
    private String symbol;
    private String side;
    private String type;
    private BigDecimal price;
    private BigDecimal qty;
    private OrderStatus status;
    private LocalDateTime filledAt;

    @Builder
    private OrderResponse(Long orderId, String symbol, String side, String type,
                          BigDecimal price, BigDecimal qty, OrderStatus status, LocalDateTime filledAt) {
        this.orderId = orderId;
        this.symbol = symbol;
        this.side = side;
        this.type = type;
        this.price = price;
        this.qty = qty;
        this.status = status;
        this.filledAt = filledAt;
    }

    public static OrderResponse from(Order o) {
        return OrderResponse.builder()
                .orderId(o.getId())
                .symbol(o.getStockCode())
                .side(o.getSide().name())
                .type(o.getType().name())
                .price(o.getPrice())
                .qty(o.getQty())
                .status(o.getStatus())
                .filledAt(o.getFilledAt())
                .build();
    }
}
