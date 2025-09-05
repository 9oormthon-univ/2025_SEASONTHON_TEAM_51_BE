package com.mockit.domain.trading.domain.repository;

import com.mockit.domain.trading.domain.entity.Positions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionsRepository extends JpaRepository<Positions, Long> {
}
