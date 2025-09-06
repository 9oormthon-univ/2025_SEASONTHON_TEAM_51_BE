package com.mockit.domain.trading.application;

import com.mockit.domain.member.domain.entity.PortfolioLedger;
import com.mockit.domain.member.domain.repository.PortfolioLedgerRepository;
import com.mockit.domain.trading.converter.TradingConverter;
import com.mockit.domain.trading.domain.entity.Orders;
import com.mockit.domain.trading.domain.entity.Positions;
import com.mockit.domain.trading.domain.entity.Stocks;
import com.mockit.domain.trading.domain.entity.Trades;
import com.mockit.domain.trading.domain.repository.OrdersRepository;
import com.mockit.domain.trading.domain.repository.PositionsRepository;
import com.mockit.domain.trading.domain.repository.StocksRepository;
import com.mockit.domain.trading.domain.repository.TradesRepository;
import com.mockit.domain.trading.dto.TradingRequestDTO;
import com.mockit.domain.trading.dto.TradingResponseDTO;
import com.mockit.domain.trading.exception.TradingException;
import com.mockit.global.error.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TradingServiceImpl implements TradingService {

    private final OrdersRepository ordersRepository;
    private final TradesRepository tradesRepository;
    private final PositionsRepository positionsRepository;
    private final PriceService priceService;
    private final TradeMatchingService matchingService;
    private final TradingConverter tradingConverter;
    private final StocksRepository stocksRepository;
    private final PortfolioLedgerRepository portfolioLedgerRepository;


    @Transactional
    public TradingResponseDTO.OrderDto createOrder(Long memberId, TradingRequestDTO.CreateOrderDto request) {
        List<TradingResponseDTO.QuoteDto> quotes = priceService.getQuotes(List.of(request.getSymbol()));

        BigDecimal currentPrice;
        if (quotes != null && !quotes.isEmpty()) {
            currentPrice = quotes.get(0).getPrice();
        } else {
            throw new TradingException(ErrorStatus.STOCK_PRICE_FETCH_FAILED);
        }

        Orders order = new Orders();
        order.setMemberId(memberId);
        order.setStockCode(request.getSymbol());
        order.setSide(Orders.OrderSide.valueOf(request.getSide()));
        order.setType(Orders.OrderType.valueOf(request.getType()));
        order.setQty(request.getQty());
        order.setFilledQty(BigDecimal.ZERO);
        order.setStatus(Orders.OrderStatus.OPEN);

        // 먼저 Orders 엔티티를 저장하여 유효한 ID를 얻습니다.
        Orders savedOrder = ordersRepository.save(order);

        if (savedOrder.getType() == Orders.OrderType.MARKET) {
            savedOrder.setPrice(currentPrice);
            savedOrder.setFilledQty(request.getQty());
            savedOrder.setStatus(Orders.OrderStatus.FILLED);
            savedOrder.setFilledAt(LocalDateTime.now());
            // ID가 있는 savedOrder 객체를 handleOrderExecution에 전달
            handleOrderExecution(savedOrder);
        } else if (savedOrder.getType() == Orders.OrderType.LIMIT) {
            if (request.getPrice() == null) {
                throw new TradingException(ErrorStatus.PRICE_REQUIRED_FOR_LIMIT_ORDER);
            }
            savedOrder.setPrice(request.getPrice());
            // ID가 있는 savedOrder 객체를 매칭 서비스에 전달
            matchingService.addOrderToQueue(savedOrder);
        }

        // 마지막에 한 번만 DTO로 변환하여 반환
        return tradingConverter.toOrderDto(savedOrder);
    }


    @Override
    @Transactional
    public void handleOrderExecution(Orders order) {
        // 1. Save the trade information to the Trades table
        Trades trade = new Trades();
        trade.setOrderId(order.getId());
        trade.setStockCode(order.getStockCode());
        trade.setPrice(order.getPrice());
        trade.setQty(order.getFilledQty());
        trade.setTs(LocalDateTime.now());
        tradesRepository.save(trade);

        // 2. Update the Positions table
        Optional<Positions> positionOptional = positionsRepository.findByMemberIdAndStockCode(order.getMemberId(), order.getStockCode());

        if (order.getSide() == Orders.OrderSide.BUY) {
            Positions position = positionOptional.orElse(new Positions());
            if (position.getMemberId() == null) {
                position.setMemberId(order.getMemberId());
                position.setStockCode(order.getStockCode());
                position.setQty(order.getFilledQty());
                position.setAvgPrice(order.getPrice());
            } else {
                BigDecimal oldQty = position.getQty();
                BigDecimal oldAvgPrice = position.getAvgPrice();
                BigDecimal newQty = order.getFilledQty();
                BigDecimal newPrice = order.getPrice();
                BigDecimal newAvgPrice = (oldQty.multiply(oldAvgPrice).add(newQty.multiply(newPrice)))
                        .divide(oldQty.add(newQty), 2, BigDecimal.ROUND_HALF_UP);
                position.setQty(oldQty.add(newQty));
                position.setAvgPrice(newAvgPrice);
            }
            position.setUpdatedAt(LocalDateTime.now());
            positionsRepository.save(position);

            // 3. Update the cash balance for a BUY order
            BigDecimal totalCost = order.getPrice().multiply(order.getFilledQty());
            PortfolioLedger ledgerEntry = new PortfolioLedger();
            ledgerEntry.setMemberId(order.getMemberId());
            ledgerEntry.setDelta(totalCost.negate()); // Subtract the cost
            ledgerEntry.setReason(PortfolioLedger.TransactionReason.TRADE);
            ledgerEntry.setRefId(order.getId().toString()); // Reference the orderId
            ledgerEntry.setTs(LocalDateTime.now());
            portfolioLedgerRepository.save(ledgerEntry);

        } else if (order.getSide() == Orders.OrderSide.SELL) {
            if (positionOptional.isPresent()) {
                Positions position = positionOptional.get();
                BigDecimal updatedQty = position.getQty().subtract(order.getFilledQty());
                if (updatedQty.compareTo(BigDecimal.ZERO) <= 0) {
                    positionsRepository.delete(position);
                } else {
                    position.setQty(updatedQty);
                    positionsRepository.save(position);
                }
            }

            // 3. Update the cash balance for a SELL order
            BigDecimal totalProceeds = order.getPrice().multiply(order.getFilledQty());
            PortfolioLedger ledgerEntry = new PortfolioLedger();
            ledgerEntry.setMemberId(order.getMemberId());
            ledgerEntry.setDelta(totalProceeds); // Add the proceeds
            ledgerEntry.setReason(PortfolioLedger.TransactionReason.TRADE);
            ledgerEntry.setRefId(order.getId().toString()); // Reference the orderId
            ledgerEntry.setTs(LocalDateTime.now());
            portfolioLedgerRepository.save(ledgerEntry);
        }
    }

    @Transactional(readOnly = true)
    public List<TradingResponseDTO.OrderListDto> getOrders(Long memberId, String status) {
        Orders.OrderStatus orderStatus = Orders.OrderStatus.valueOf(status.toUpperCase());
        List<Orders> orders = ordersRepository.findByMemberIdAndStatus(memberId, orderStatus);
        return orders.stream()
                .map(tradingConverter::toOrderListDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TradingResponseDTO.PositionDto> getPositions(Long memberId) {
        List<Positions> positions = positionsRepository.findByMemberId(memberId);
        return positions.stream()
                .map(position -> {
                    Stocks stock = stocksRepository.findByStockCode(position.getStockCode())
                            .orElseThrow(() -> new TradingException(ErrorStatus.STOCK_NOT_FOUND));
                    return tradingConverter.toPositionDto(position, stock);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TradingResponseDTO.PortfolioDto getPortfolio(Long memberId) {
        BigDecimal totalCash = portfolioLedgerRepository.findTotalCashByMemberId(memberId)
                .orElse(BigDecimal.ZERO);
        List<TradingResponseDTO.PositionDto> positions = getPositions(memberId);

        BigDecimal totalAssetValue = BigDecimal.ZERO;
        BigDecimal totalProfitAndLoss = BigDecimal.ZERO;

        for (TradingResponseDTO.PositionDto position : positions) {
            totalAssetValue = totalAssetValue.add(position.getCurrentPrice().multiply(position.getQty()));
            totalProfitAndLoss = totalProfitAndLoss.add(position.getProfitAndLoss());
        }

        BigDecimal totalPortfolioValue = totalCash.add(totalAssetValue);

        return TradingResponseDTO.PortfolioDto.builder()
                .totalCash(totalCash)
                .totalAssetValue(totalAssetValue)
                .totalPortfolioValue(totalPortfolioValue)
                .totalProfitAndLoss(totalProfitAndLoss)
                .build();
    }

    @Transactional
    public TradingResponseDTO.CancelOrderDto cancelOrder(Long memberId, Long orderId) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new TradingException(ErrorStatus.ORDER_NOT_FOUND));

        if (!order.getMemberId().equals(memberId)) {
            throw new TradingException(ErrorStatus.ORDER_FORBIDDEN_ACCESS);
        }

        if (order.getStatus() != Orders.OrderStatus.OPEN && order.getStatus() != Orders.OrderStatus.PARTIAL) {
            throw new TradingException(ErrorStatus.ORDER_ALREADY_FILLED_OR_CANCELED);
        }

        order.setStatus(Orders.OrderStatus.CANCELED);

        matchingService.removeOrderFromQueue(order);

        return TradingResponseDTO.CancelOrderDto.builder()
                .success(true)
                .message("Order canceled successfully.")
                .build();
    }

}