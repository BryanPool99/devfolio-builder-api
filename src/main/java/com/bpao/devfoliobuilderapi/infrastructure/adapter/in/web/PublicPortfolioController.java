package com.bpao.devfoliobuilderapi.infrastructure.adapter.in.web;

import com.bpao.devfoliobuilderapi.application.dto.portfolio.PublicPortfolioResponse;
import com.bpao.devfoliobuilderapi.application.port.in.portfolio.GetPublicPortfolioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Endpoint publico del portafolio por username (sin autenticacion).
 */
@RestController
@RequiredArgsConstructor
public class PublicPortfolioController {

    private final GetPublicPortfolioUseCase getPublicPortfolioUseCase;

    @GetMapping("/api/v1/public/portfolios/{username}")
    public Mono<PublicPortfolioResponse> publicPortfolio(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return getPublicPortfolioUseCase.getByUsername(username, page, size);
    }
}