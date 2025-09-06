package com.mockit.domain.trading.application;

import com.mockit.domain.trading.domain.entity.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.PriorityQueue;

@Service
@RequiredArgsConstructor
public class TradeMatchingService {

    private final ApplicationEventPublisher eventPublisher;

    // 매수 주문: 높은 가격이 우선 (내림차순)
    private final PriorityQueue<Orders> buyOrders = new PriorityQueue<>(Comparator.comparing(Orders::getPrice).reversed());

    // 매도 주문: 낮은 가격이 우선 (오름차순)
    private final PriorityQueue<Orders> sellOrders = new PriorityQueue<>(Comparator.comparing(Orders::getPrice));

    // private final TradingService tradingService; // 이 필드를 삭제하여 순환 의존성 해결

    public void addOrderToQueue(Orders order) {
        if (order.getSide() == Orders.OrderSide.BUY) {
            buyOrders.add(order);
        } else {
            sellOrders.add(order);
        }
        matchOrders();
    }

    /**
     * 주문을 매칭시키는 로직
     */
    public void matchOrders() {
        while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
            Orders buyOrder = buyOrders.peek();
            Orders sellOrder = sellOrders.peek();

            if (buyOrder.getPrice().compareTo(sellOrder.getPrice()) >= 0) {
                Orders filledBuyOrder = buyOrders.poll();
                Orders filledSellOrder = sellOrders.poll();

                filledBuyOrder.setStatus(Orders.OrderStatus.FILLED);
                filledBuyOrder.setFilledQty(filledBuyOrder.getQty());
                filledBuyOrder.setFilledAt(java.time.LocalDateTime.now());

                filledSellOrder.setStatus(Orders.OrderStatus.FILLED);
                filledSellOrder.setFilledQty(filledSellOrder.getQty());
                filledSellOrder.setFilledAt(java.time.LocalDateTime.now());

                // 이벤트 발행: TradingService에 직접 의존하지 않고 체결 사실만 알림
                eventPublisher.publishEvent(new OrderFilledEvent(this, filledBuyOrder));
                eventPublisher.publishEvent(new OrderFilledEvent(this, filledSellOrder));
            } else {
                break;
            }
        }
    }

    /**
     * 매칭 대기열에서 주문을 제거하는 로직
     * @param order 취소할 주문
     * @return 제거 성공 여부
     */
    public boolean removeOrderFromQueue(Orders order) {
        if (order.getSide() == Orders.OrderSide.BUY) {
            return buyOrders.remove(order);
        } else if (order.getSide() == Orders.OrderSide.SELL) {
            return sellOrders.remove(order);
        }
        return false;
    }
}