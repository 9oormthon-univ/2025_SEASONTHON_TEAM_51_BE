package mockit.mockit.domain.trading.domain.repository;

import mockit.mockit.domain.trading.domain.entity.Trades;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradesRepository extends JpaRepository<Trades, Long> {
}
