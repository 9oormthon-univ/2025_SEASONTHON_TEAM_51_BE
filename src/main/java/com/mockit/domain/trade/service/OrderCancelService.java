package com.mockit.domain.trade.service;

import com.mockit.domain.trade.entity.Order;
import com.mockit.domain.trade.enums.OrderStatus;
import com.mockit.domain.trade.exception.TradeErrorStatus;
import com.mockit.domain.trade.repository.OrderRepository;
import com.mockit.global.exception.GeneralException;
import com.mockit.support.session.UserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderCancelService {

    private final OrderRepository orderRepository;
    private final UserSession userSession;

    public void cancel(Long orderId) {
        Long userId = userSession.currentUserId();
        Order o = orderRepository.findById(orderId)
                .orElseThrow(() -> new GeneralException(TradeErrorStatus.ORDER_NOT_FOUND));
        if (!o.getUserId().equals(userId)) {
            throw new GeneralException(TradeErrorStatus.ORDER_NOT_FOUND); // 타인 주문 은닉
        }
        if (o.getStatus() == OrderStatus.FILLED || o.getStatus() == OrderStatus.CANCELED) {
            return;  // 이미 종결
        }
        // 예약/미체결만 취소
        o.cancel();
    }
}
