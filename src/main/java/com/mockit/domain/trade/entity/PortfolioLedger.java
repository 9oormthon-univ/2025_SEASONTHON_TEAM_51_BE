package com.mockit.domain.trade.entity;

import com.mockit.domain.model.entity.BaseEntity;
import com.mockit.domain.trade.enums.LedgerReason;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Entity(name = "TradePortfolioLedger")
@Table(name = "portfolio_ledger")
@Access(AccessType.FIELD)
public class PortfolioLedger extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal delta;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private LedgerReason reason;

    @Column(length = 64)
    private String refId;

    @Column(nullable = false)
    private LocalDateTime ts;

    public PortfolioLedger(Long userId, BigDecimal delta, LedgerReason reason, String refId, LocalDateTime ts) {
        this.userId = userId;
        this.delta = delta;
        this.reason = reason;
        this.refId = refId;
        this.ts = ts;
    }
}
