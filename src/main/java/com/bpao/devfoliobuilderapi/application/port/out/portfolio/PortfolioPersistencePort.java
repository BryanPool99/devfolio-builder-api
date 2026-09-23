package com.bpao.devfoliobuilderapi.application.port.out.portfolio;

import com.bpao.devfoliobuilderapi.domain.model.Portfolio;
import reactor.core.publisher.Mono;

/**
 * Puerto de salida: persistencia de portfolios implementada por la infraestructura.
 */
public interface PortfolioPersistencePort {

    Mono<Portfolio> findByUserId(Long userId);

    Mono<Portfolio> save(Portfolio portfolio);
}