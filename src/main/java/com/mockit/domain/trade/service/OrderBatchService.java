package com.mockit.domain.trade.service;

import com.mockit.domain.market.service.PriceProvider;
import com.mockit.domain.trade.entity.Order;
import com.mockit.domain.trade.enums.OrderStatus;
import com.mockit.domain.trade.enums.TimeInForce;
import com.mockit.domain.trade.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderBatchService {

    private final OrderRepository orderRepository;
    private final PriceProvider priceProvider;
    private final MatchingEngine matchingEngine;
    private final OrderCommandService orderCommandService; // fillAll 재사용

    // KST 09:00:00 – 예약 주문 활성화/즉시체결 시도
    @Scheduled(cron = "0 0 9 * * MON-FRI", zone = "Asia/Seoul")
    @Transactional
    public void activateReservedAtOpen() {
        List<Order> list = orderRepository.findActivatableOrders(LocalDateTime.now());
        for (Order o : list) {
            var last = priceProvider.lastPrice(o.getStockCode());
            if (o.getPrice() != null && matchingEngine.canFillLimit(o.getSide(), o.getPrice(), last)) {
                orderCommandService.fillAll(o.getUserId(), o, o.getPrice());
            }
        }
    }

    // KST 15:30:00 – DAY 주문 자동 취소
    @Scheduled(cron = "0 30 15 * * MON-FRI", zone = "Asia/Seoul")
    @Transactional
    public void cancelDayOrdersAtClose() {
        List<Order> list = orderRepository.findCancelableAtClose(TimeInForce.DAY);
        for (Order o : list) {
            if (o.getStatus() == OrderStatus.OPEN) {
                o.cancel();
            }
        }
    }
}
