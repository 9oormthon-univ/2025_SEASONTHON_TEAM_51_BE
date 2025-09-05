package com.mockit.domain.trade.repository;

import com.mockit.domain.trade.entity.Position;
import com.mockit.domain.trade.entity.PositionId;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, PositionId> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Position p where p.id.userId = :userId and p.id.stockCode = :code")
    Optional<Position> findForUpdate(@Param("userId") Long userId, @Param("code") String stockCode);

    List<Position> findById_UserId(Long userId);
}
