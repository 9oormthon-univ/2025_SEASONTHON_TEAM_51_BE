package com.mockit.domain.trade.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class PositionId implements Serializable {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "stock_code", length = 32, nullable = false)
    private String stockCode;

    public PositionId(Long userId, String stockCode) {
        this.userId = userId;
        this.stockCode = stockCode;
    }
}
