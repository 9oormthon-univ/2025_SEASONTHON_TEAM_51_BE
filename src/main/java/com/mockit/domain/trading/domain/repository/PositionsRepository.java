package com.mockit.domain.trading.domain.repository;

import com.mockit.domain.trading.domain.entity.Positions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PositionsRepository extends JpaRepository<Positions, Long> {

    Optional<Positions> findByMemberIdAndStockCode(Long memberId, String stockCode);

    List<Positions> findByMemberId(Long memberId);
}
