package com.mockit.domain.trading.domain.entity;

import com.mockit.domain.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Stocks")
@Getter
@Setter
public class Stocks extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stockId;

    @Column(nullable = false, unique = true)
    private String stockCode;

    @Column(nullable = false)
    private String stockName;
}
