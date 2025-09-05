package com.mockit.domain.trade.dto.request;

import com.mockit.domain.trade.entity.Order;
import com.mockit.domain.trade.enums.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class PlaceOrderRequest {

    @NotBlank
    private String symbol;

    @NotNull
    private Side side;

    @NotNull
    private OrderType type;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    @NotNull
    @DecimalMin("1")
    private BigDecimal qty;

    private String clientOrderId;

    public Order toEntity(Long userId) {
        return new Order(
                userId,
                symbol,
                side,
                type,
                price,
                qty,
                clientOrderId,
                null,
                TimeInForce.DAY,
                OrderStatus.OPEN
        );
    }
}
