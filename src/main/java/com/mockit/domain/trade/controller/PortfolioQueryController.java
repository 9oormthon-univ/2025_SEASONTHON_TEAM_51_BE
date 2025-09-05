package com.mockit.domain.trade.controller;

import com.mockit.domain.trade.dto.response.PositionItemResponse;
import com.mockit.domain.trade.dto.response.PortfolioResponse;
import com.mockit.domain.trade.service.PortfolioQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PortfolioQueryController {

    private final PortfolioQueryService portfolioQueryService;

    @GetMapping("/positions")
    public List<PositionItemResponse> positions() {
        return portfolioQueryService.getPositions();
    }

    @GetMapping("/portfolio")
    public PortfolioResponse portfolio() {
        return portfolioQueryService.getPortfolio();
    }
}
