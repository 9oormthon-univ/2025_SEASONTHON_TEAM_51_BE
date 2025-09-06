// src/main/java/com/mockit/domain/member/domain/entity/PortfolioLedger.java
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

    // ✅ DB 컬럼명이 user_id 이므로 명시 매핑
    @Column(name = "user_id", nullable = false)
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