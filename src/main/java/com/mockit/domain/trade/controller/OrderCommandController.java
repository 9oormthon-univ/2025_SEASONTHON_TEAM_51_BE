package com.mockit.domain.trade.controller;

import com.mockit.domain.trade.dto.request.PlaceOrderRequest;
import com.mockit.domain.trade.dto.response.OrderResponse;
import com.mockit.domain.trade.service.OrderCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderCommandController {

    private final OrderCommandService orderCommandService;

    @PostMapping
    public OrderResponse place(@Validated @RequestBody PlaceOrderRequest request) {
        return orderCommandService.place(request);
    }
}
