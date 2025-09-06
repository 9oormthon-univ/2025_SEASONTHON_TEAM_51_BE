package com.mockit.domain.member.domain.repository;

import com.mockit.domain.member.domain.entity.PortfolioLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface PortfolioLedgerRepository extends JpaRepository<PortfolioLedger, Long> {

    @Query("SELECT SUM(pl.delta) FROM PortfolioLedger pl WHERE pl.memberId = :memberId")
    Optional<BigDecimal> findTotalCashByMemberId(@Param("memberId") Long memberId);
}