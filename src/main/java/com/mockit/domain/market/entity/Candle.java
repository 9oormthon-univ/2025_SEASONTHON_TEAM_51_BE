package com.mockit.domain.market.entity;

import com.mockit.domain.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Entity
@Table(name = "candles")
@Access(AccessType.FIELD)
public class Candle extends BaseEntity {

    @EmbeddedId
    private CandleId id;

    @Column(name = "open_price",  nullable = false, precision = 18, scale = 4)
    private BigDecimal openPrice;

    @Column(name = "high_price",  nullable = false, precision = 18, scale = 4)
    private BigDecimal highPrice;

    @Column(name = "low_price",   nullable = false, precision = 18, scale = 4)
    private BigDecimal lowPrice;

    @Column(name = "close_price", nullable = false, precision = 18, scale = 4)
    private BigDecimal closePrice;

    @Column(name = "volume", nullable = false)
    private long volume;

    public Candle(CandleId id,
                  BigDecimal openPrice, BigDecimal highPrice,
                  BigDecimal lowPrice, BigDecimal closePrice,
                  long volume) {
        this.id = id;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.closePrice = closePrice;
        this.volume = volume;
    }
}
