package com.mockit.domain.trading.application;

import com.mockit.domain.trading.domain.entity.Orders;
import org.springframework.context.ApplicationEvent;

public class OrderFilledEvent extends ApplicationEvent {

    private final Orders order;

    public OrderFilledEvent(Object source, Orders order) {
        super(source);
        this.order = order;
    }
    public Orders getOrder() {
        return this.order;
    }
}
