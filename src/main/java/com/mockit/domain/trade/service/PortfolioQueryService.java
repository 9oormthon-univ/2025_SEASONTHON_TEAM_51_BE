package com.mockit.domain.trade.service;

import com.mockit.domain.market.service.PriceProvider;
import com.mockit.domain.trade.dto.response.PositionItemResponse;
import com.mockit.domain.trade.dto.response.PortfolioResponse;
import com.mockit.domain.trade.repository.PositionRepository;
import com.mockit.domain.trade.repository.TradePortfolioLedgerRepository;
import com.mockit.support.session.UserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortfolioQueryService {

    private final PositionRepository positionRepository;
    private final TradePortfolioLedgerRepository ledgerRepository;
    private final PriceProvider priceProvider;
    private final UserSession userSession;

    public List<PositionItemResponse> getPositions() {
        Long userId = userSession.currentUserId();
        var list = positionRepository.findById_UserId(userId);
        return list.stream()
                .map(p -> {
                    var symbol = p.getId().getStockCode();
                    var curr = priceProvider.lastPrice(symbol);
                    return PositionItemResponse.of(symbol, p.getQty(), p.getAvgPrice(), curr);
                })
                .toList();
    }

    public PortfolioResponse getPortfolio() {
        Long userId = userSession.currentUserId();
        var cash = ledgerRepository.sumDeltaByUserId(userId);

        var positions = positionRepository.findById_UserId(userId);
        BigDecimal asset = BigDecimal.ZERO;
        BigDecimal pnl = BigDecimal.ZERO;

        for (var p : positions) {
            var curr = priceProvider.lastPrice(p.getId().getStockCode());
            asset = asset.add(curr.multiply(p.getQty()));
            pnl = pnl.add(curr.subtract(p.getAvgPrice()).multiply(p.getQty()));
        }
        return PortfolioResponse.of(cash, asset, pnl);
    }
}
