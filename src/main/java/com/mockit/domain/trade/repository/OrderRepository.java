package com.mockit.domain.trade.repository;

import com.mockit.domain.trade.entity.Order;
import com.mockit.domain.trade.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByUserIdAndClientOrderId(Long userId, String clientOrderId);

    List<Order> findByUserIdAndStatusOrderByIdDesc(Long userId, OrderStatus status);
}
