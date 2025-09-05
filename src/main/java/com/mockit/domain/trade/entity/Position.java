package com.mockit.domain.trade.entity;

import com.mockit.domain.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Entity
@Table(name = "positions")
@Access(AccessType.FIELD)
public class Position extends BaseEntity {

    @EmbeddedId
    private PositionId id;

    @Column(nullable = false, precision = 18, scale = 0)
    private BigDecimal qty;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal avgPrice;

    private LocalDateTime updatedAt;

    public Position(PositionId id, BigDecimal qty, BigDecimal avgPrice) {
        this.id = id;
        this.qty = qty;
        this.avgPrice = avgPrice;
        this.updatedAt = LocalDateTime.now();
    }

    public void applyBuy(BigDecimal buyQty, BigDecimal buyPrice) {
        var oldQty = this.qty;
        var newQty = oldQty.add(buyQty);
        var newAvg = (oldQty.multiply(this.avgPrice).add(buyQty.multiply(buyPrice)))
                .divide(newQty, 4, RoundingMode.HALF_UP);
        this.qty = newQty;
        this.avgPrice = newAvg;
        this.updatedAt = LocalDateTime.now();
    }

    public void applySell(BigDecimal sellQty) {
        this.qty = this.qty.subtract(sellQty);
        this.updatedAt = LocalDateTime.now();
    }
}
