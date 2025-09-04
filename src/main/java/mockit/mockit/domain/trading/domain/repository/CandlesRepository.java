package mockit.mockit.domain.trading.domain.repository;

import mockit.mockit.domain.trading.domain.entity.Candles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandlesRepository extends JpaRepository<Candles, Long> {
}
