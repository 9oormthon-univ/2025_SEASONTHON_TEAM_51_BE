package com.mockit.domain.trade.dto.request;

import com.mockit.domain.trade.entity.Order;
import com.mockit.domain.trade.enums.*;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class PlaceOrderRequest {

    @NotBlank(message = "symbol은 필수입니다.")
    private String symbol;

    @NotNull(message = "side는 필수입니다.")
    private Side side;

    @NotNull(message = "type은 필수입니다.")
    private OrderType type;    // MARKET | LIMIT

    @DecimalMin(value = "0.0", inclusive = false, message = "price는 0보다 커야 합니다.")
    private BigDecimal price;

    @NotNull(message = "qty는 필수입니다.")
    @DecimalMin(value = "1", message = "qty는 1 이상이어야 합니다.")
    private BigDecimal qty;

    private String clientOrderId;

    @AssertTrue(message = "LIMIT 주문은 price(지정가)가 필수이며 0보다 큰 값이어야 합니다.")
    public boolean isLimitPriceProvided() {
        // type이 아직 바인딩 안 됐거나 잘못 들어온 경우엔 "type 필수" 에러만 내도록 true 반환
        if (type == null) return true;

        // LIMIT이면 price가 반드시 존재하고, 0보다 커야 함
        if (type == OrderType.LIMIT) {
            return price != null && price.signum() > 0;
        }

        // LIMIT가 아니면 신경 안 씀
        return true;
    }

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
