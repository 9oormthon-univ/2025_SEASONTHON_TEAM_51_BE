package com.mockit.domain.trading.domain.repository;

import com.mockit.domain.trading.domain.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    List<Orders> findByMemberIdAndStatus(Long memberId, Orders.OrderStatus status);

    Optional<Orders> findById(Long id);
}
