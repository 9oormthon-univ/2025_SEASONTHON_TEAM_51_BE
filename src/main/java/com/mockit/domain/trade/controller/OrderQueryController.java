package com.mockit.domain.trade.controller;

import com.mockit.domain.trade.dto.response.OrderListItemResponse;
import com.mockit.domain.trade.enums.OrderStatus;
import com.mockit.domain.trade.repository.OrderRepository;
import com.mockit.support.session.UserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Transactional(readOnly = true)
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderQueryController {

    private final OrderRepository orderRepository;
    private final UserSession userSession;

    @GetMapping
    public List<OrderListItemResponse> list(@RequestParam(required = false) OrderStatus status) {
        Long userId = userSession.currentUserId();

        if (status != null) {
            return orderRepository.findByUserIdAndStatusOrderByIdDesc(userId, status)
                    .stream().map(OrderListItemResponse::from).toList();
        }
        return orderRepository.findTop100ByUserIdOrderByIdDesc(userId)
                .stream().map(OrderListItemResponse::from).toList();
    }
}
