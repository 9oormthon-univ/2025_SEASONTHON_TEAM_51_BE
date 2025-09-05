package com.mockit.domain.member.domain.repository;

import com.mockit.domain.member.domain.entity.PortfolioLedger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioLedgerRepository extends JpaRepository<PortfolioLedger, Long> {
}
