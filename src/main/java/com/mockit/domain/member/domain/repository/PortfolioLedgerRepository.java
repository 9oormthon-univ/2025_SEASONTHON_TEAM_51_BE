package com.mockit.domain.member.domain.repository;

import com.mockit.domain.member.domain.entity.PortfolioLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;

public interface PortfolioLedgerRepository extends JpaRepository<PortfolioLedger, Long> {
    @Query("select coalesce(sum(p.delta), 0) from PortfolioLedger p where p.memberId = :memberId")
    BigDecimal sumBalanceByMemberId(Long memberId);
}