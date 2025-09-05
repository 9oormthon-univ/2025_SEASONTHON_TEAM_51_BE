package com.mockit.domain.market.repository;

import com.mockit.domain.market.entity.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstrumentRepository extends JpaRepository<Instrument, String> {
}
