package com.mockit.domain.trading.domain.repository;

import com.mockit.domain.trading.domain.entity.Stocks;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StocksRepository extends JpaRepository<Stocks, Long> {
}
