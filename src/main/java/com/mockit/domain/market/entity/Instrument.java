package com.mockit.domain.market.entity;

import com.mockit.domain.model.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Entity
@Table(name = "instruments")
public class Instrument extends BaseEntity {

    @Id
    private String stockCode;  // PK

    private String stockName;
    private String market;
    private String currency;

    public Instrument(String stockCode, String stockName, String market, String currency) {
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.market = market;
        this.currency = currency;
    }
}