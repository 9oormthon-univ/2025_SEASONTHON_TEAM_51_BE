package mockit.mockit.domain.trading.domain.repository;

import mockit.mockit.domain.trading.domain.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
}
