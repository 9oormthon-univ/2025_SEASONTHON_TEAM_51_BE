package com.mockit.domain.trading.application;

import com.mockit.domain.trading.domain.entity.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TradingEventHandler {

    private final TradingService tradingService;

    @EventListener
    @Transactional
    public void handleOrderFilledEvent(OrderFilledEvent event) {
        Orders order = event.getOrder();
        tradingService.handleOrderExecution(order);
    }
}