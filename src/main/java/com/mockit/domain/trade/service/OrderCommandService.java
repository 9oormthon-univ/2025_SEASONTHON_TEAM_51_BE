package com.mockit.domain.trade.service;

import com.mockit.domain.market.service.PriceProvider;
import com.mockit.domain.trade.dto.request.PlaceOrderRequest;
import com.mockit.domain.trade.dto.response.OrderResponse;
import com.mockit.domain.trade.entity.*;
import com.mockit.domain.trade.enums.*;
import com.mockit.domain.trade.exception.TradeErrorStatus;
import com.mockit.domain.trade.repository.*;
import com.mockit.global.exception.GeneralException;
import com.mockit.support.session.UserSession;
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

    private final PriceProvider priceProvider;
    private final UserSession userSession;

    public OrderResponse place(PlaceOrderRequest req) {
        Long userId = userSession.currentUserId();

        // 멱등성: clientOrderId 중복 방지
        if (req.getClientOrderId() != null &&
                orderRepository.findByUserIdAndClientOrderId(userId, req.getClientOrderId()).isPresent()) {
            throw new GeneralException(TradeErrorStatus.DUPLICATE_CLIENT_ORDER);
        }

        var order = orderRepository.save(req.toEntity(userId));

        // 1차: 시장가만 처리
        if (req.getType() != OrderType.MARKET) {
            throw new GeneralException(TradeErrorStatus.INVALID_ORDER_STATE);
        }

        final BigDecimal last = priceProvider.lastPrice(req.getSymbol());
        fillAll(userId, order, last);

        return OrderResponse.from(order);
    }

    private void fillAll(Long userId, Order order, BigDecimal price) {
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
