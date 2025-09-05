package com.mockit.domain.trade.entity;

import com.mockit.domain.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Entity
@Table(name = "trades")
@Access(AccessType.FIELD)
public class Trade extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false, length = 32)
    private String stockCode;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal price;

    @Column(nullable = false, precision = 18, scale = 0)
    private BigDecimal qty;

    @Column(nullable = false)
    private LocalDateTime ts;

    public Trade(Long orderId, String stockCode, BigDecimal price, BigDecimal qty, LocalDateTime ts) {
        this.orderId = orderId;
        this.stockCode = stockCode;
        this.price = price;
        this.qty = qty;
        this.ts = ts;
    }
}
