package com.mockit.domain.trading.domain.repository;

import com.mockit.domain.trading.domain.entity.Stocks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StocksRepository extends JpaRepository<Stocks, Long> {

    Optional<Stocks> findByStockCode(String stockCode);

}
