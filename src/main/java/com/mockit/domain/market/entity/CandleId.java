package com.mockit.domain.market.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@EqualsAndHashCode
@Embeddable
public class CandleId implements Serializable {

    @Column(name = "stock_code", length = 32, nullable = false)
    private String stockCode;

    @Column(name = "tf", length = 4, nullable = false)  // '1m','1d'
    private String tf;

    @Column(name = "ts", nullable = false)
    private LocalDateTime ts;

    public CandleId(String stockCode, String tf, LocalDateTime ts) {
        this.stockCode = stockCode;
        this.tf = tf;
        this.ts = ts;
    }
}
