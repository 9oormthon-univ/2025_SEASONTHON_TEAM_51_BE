package com.mockit.domain.trade.entity;

import com.mockit.domain.model.entity.BaseEntity;
import com.mockit.domain.trade.enums.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Entity
@Table(name = "orders")
@Access(AccessType.FIELD)
public class Order extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 32)
    private String stockCode;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private Side side;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private OrderType type;

    @Column(precision = 18, scale = 4)
    private BigDecimal price; // LIMIT only

    @Column(precision = 18, scale = 0, nullable = false)
    private BigDecimal qty;

    @Column(precision = 18, scale = 0, nullable = false)
    private BigDecimal filledQty;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private OrderStatus status;

    @Column(length = 64)
    private String clientOrderId;

    private LocalDateTime validAfterTs;

    @Enumerated(EnumType.STRING)
    private TimeInForce timeInForce;

    private LocalDateTime filledAt;

    public Order(Long userId, String stockCode, Side side, OrderType type,
                 BigDecimal price, BigDecimal qty, String clientOrderId,
                 LocalDateTime validAfterTs, TimeInForce tif, OrderStatus status) {
        this.userId = userId;
        this.stockCode = stockCode;
        this.side = side;
        this.type = type;
        this.price = price;
        this.qty = qty;
        this.filledQty = BigDecimal.ZERO;
        this.status = status;
        this.clientOrderId = clientOrderId;
        this.validAfterTs = validAfterTs;
        this.timeInForce = tif;
    }

    public void markFilled(LocalDateTime ts) {
        this.filledQty = this.qty;
        this.status = OrderStatus.FILLED;
        this.filledAt = ts;
    }
}
