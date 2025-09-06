package com.mockit.domain.trade.service;

import com.mockit.domain.market.service.PriceProvider;
import com.mockit.domain.trade.dto.request.PlaceOrderRequest;
import com.mockit.domain.trade.dto.response.OrderResponse;
import com.mockit.domain.trade.entity.*;
import com.mockit.domain.trade.enums.*;
import com.mockit.domain.trade.exception.TradeErrorStatus;
import com.mockit.domain.trade.repository.*;
import com.mockit.domain.user.repository.UserAccountRepository;
import com.mockit.global.exception.GeneralException;
import com.mockit.support.session.UserSession;
import com.mockit.support.time.MarketCalendar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderCommandService {

    private final OrderRepository orderRepository;
    private final TradeRepository tradeRepository;
    private final PositionRepository positionRepository;
    private final TradePortfolioLedgerRepository ledgerRepository;
    private final UserAccountRepository userAccountRepository;

    private final MatchingEngine matchingEngine;
    private final MarketCalendar marketCalendar;

    private final PriceProvider priceProvider;
    private final UserSession userSession;

    public OrderResponse place(PlaceOrderRequest req) {
        Long userId = userSession.currentUserId();

        // 0) 유저 잠금
        userAccountRepository.lockById(userId)
                .orElseThrow(() -> new GeneralException(TradeErrorStatus.ORDER_NOT_FOUND));

        // 1) 멱등성
        if (req.getClientOrderId() != null &&
                orderRepository.findByUserIdAndClientOrderId(userId, req.getClientOrderId()).isPresent()) {
            throw new GeneralException(TradeErrorStatus.DUPLICATE_CLIENT_ORDER);
        }

        // 2) LIMIT 교차 검증 (가격 필수)
        if (req.getType() == OrderType.LIMIT) {
            if (req.getPrice() == null || req.getPrice().signum() <= 0) {
                throw new GeneralException(TradeErrorStatus.INVALID_ORDER_STATE);
            }
        }

        // 3) 주문 저장 (OPEN 상태의 새 주문 생성)
        var order = orderRepository.save(req.toEntity(userId));

        // 4) MARKET: 즉시 체결
        if (req.getType() == OrderType.MARKET) {
            final BigDecimal last = priceProvider.lastPrice(req.getSymbol());
            fillAll(userId, order, last);
            return OrderResponse.from(order);
        }

        // 5) LIMIT: 장외면 예약만 하고 return (시세 호출 불필요)
        var now = java.time.LocalDateTime.now();
        boolean isOpen = marketCalendar.isOpen(now);
        if (!isOpen) {
            order.scheduleAt(marketCalendar.nextOpen(now));
            order.markOpen();
            return OrderResponse.from(order);
        }

        // 6) LIMIT + 장중: 시세 조회 후 즉시 체결 여부 판단
        var last = priceProvider.lastPrice(req.getSymbol());
        boolean canFill = matchingEngine.canFillLimit(order.getSide(), order.getPrice(), last);
        if (canFill) {
            fillAll(userId, order, order.getPrice());  // 지정가 체결
        } else {
            order.markOpen();  // 미체결 유지
        }
        return OrderResponse.from(order);
    }


    public void fillAll(Long userId, Order order, BigDecimal price) {
        var now = LocalDateTime.now();
        var qty = order.getQty();

        if (order.getSide() == Side.BUY) {
            // 현금 확인 (ledger 합계)
            var cash = ledgerRepository.sumDeltaByUserId(userId);
            var cost = price.multiply(qty);
            if (cash.compareTo(cost) < 0) throw new GeneralException(TradeErrorStatus.INSUFFICIENT_CASH);

            // 포지션 갱신 (잠금)
            var pos = positionRepository.findForUpdate(userId, order.getStockCode())
                    .orElseGet(() -> new Position(new PositionId(userId, order.getStockCode()),
                            BigDecimal.ZERO, BigDecimal.ZERO));
            pos.applyBuy(qty, price);
            positionRepository.save(pos);

            // 원장(현금 차감)
            ledgerRepository.save(new PortfolioLedger(userId, cost.negate(), LedgerReason.TRADE,
                    String.valueOf(order.getId()), now));

        } else { // SELL
            var pos = positionRepository.findForUpdate(userId, order.getStockCode())
                    .orElseThrow(() -> new GeneralException(TradeErrorStatus.INSUFFICIENT_QTY));
            if (pos.getQty().compareTo(qty) < 0) throw new GeneralException(TradeErrorStatus.INSUFFICIENT_QTY);

            pos.applySell(qty);
            positionRepository.save(pos);

            // 원장(현금 증가)
            var proceeds = price.multiply(qty);
            ledgerRepository.save(new PortfolioLedger(userId, proceeds, LedgerReason.TRADE,
                    String.valueOf(order.getId()), now));
        }

        // 체결 기록 & 주문 완료
        tradeRepository.save(new Trade(order.getId(), order.getStockCode(), price, qty, now));
        order.markFilled(now);
    }
}
