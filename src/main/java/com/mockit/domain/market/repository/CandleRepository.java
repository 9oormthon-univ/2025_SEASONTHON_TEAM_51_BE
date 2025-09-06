package com.mockit.domain.market.repository;

import com.mockit.domain.market.entity.Candle;
import com.mockit.domain.market.entity.CandleId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CandleRepository extends JpaRepository<Candle, CandleId> {

    // 가장 최근 1개
    Optional<Candle> findTopByIdStockCodeAndIdTfOrderByIdTsDesc(String stockCode, String tf);

    // 최근 100개 (내림차순)
    List<Candle> findTop100ByIdStockCodeAndIdTfOrderByIdTsDesc(String stockCode, String tf);

    // 특정 시점 이후 (오름차순)
    List<Candle> findByIdStockCodeAndIdTfAndIdTsAfterOrderByIdTsAsc(String stockCode, String tf, LocalDateTime ts);

    Optional<Candle> findByIdStockCodeAndIdTfAndIdTs(String stockCode, String tf, LocalDateTime ts);

    List<Candle> findAllByIdStockCodeAndIdTfOrderByIdTsDesc(String stockCode, String tf, Pageable pageable);


}