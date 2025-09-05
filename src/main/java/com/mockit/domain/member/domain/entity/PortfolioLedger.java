package com.mockit.domain.member.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PortfolioLedger")
@Getter
@Setter
public class PortfolioLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private BigDecimal delta;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionReason reason;

    private String refId;

    @Column(nullable = false)
    private LocalDateTime ts;

    public enum TransactionReason { DEPOSIT, WITHDRAW, TRADE, FEE, QUIZ_REWARD, MISSION_REWARD }
}
