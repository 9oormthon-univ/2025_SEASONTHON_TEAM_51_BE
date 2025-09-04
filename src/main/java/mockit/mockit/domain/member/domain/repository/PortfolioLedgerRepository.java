package mockit.mockit.domain.member.domain.repository;

import mockit.mockit.domain.member.domain.entity.PortfolioLedger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioLedgerRepository extends JpaRepository<PortfolioLedger, Long> {
}
