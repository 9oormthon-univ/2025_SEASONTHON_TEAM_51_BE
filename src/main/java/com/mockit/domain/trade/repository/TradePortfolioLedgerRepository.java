package com.mockit.domain.trade.repository;

import com.mockit.domain.trade.entity.PortfolioLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface TradePortfolioLedgerRepository extends JpaRepository<PortfolioLedger, Long> {

    @Query("select coalesce(sum(l.delta), 0) from TradePortfolioLedger l where l.userId = :userId")
    BigDecimal sumDeltaByUserId(@Param("userId") Long userId);
}
