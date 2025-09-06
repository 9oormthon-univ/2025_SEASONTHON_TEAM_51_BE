package com.mockit.domain.trade.repository;

import com.mockit.domain.trade.entity.Order;
import com.mockit.domain.trade.enums.OrderStatus;
import com.mockit.domain.trade.enums.TimeInForce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByUserIdAndClientOrderId(Long userId, String clientOrderId);

    List<Order> findByUserIdAndStatusOrderByIdDesc(Long userId, OrderStatus status);

    List<Order> findTop100ByUserIdOrderByIdDesc(Long userId);

    // 개장 직후 활성화 대상(예약 주문)
    @Query("""
           select o
             from Order o
            where o.status = com.mockit.domain.trade.enums.OrderStatus.OPEN
              and o.validAfterTs is not null
              and o.validAfterTs <= :now
           """)
    List<Order> findActivatableOrders(@Param("now") LocalDateTime now);

    // 마감 자동 취소 대상(DAY + OPEN/PARTIAL)
    @Query("""
           select o
             from Order o
            where o.timeInForce = :tif
              and o.status in (
                    com.mockit.domain.trade.enums.OrderStatus.OPEN,
                    com.mockit.domain.trade.enums.OrderStatus.PARTIAL
              )
           """)
    List<Order> findCancelableAtClose(@Param("tif") TimeInForce tif);
}
