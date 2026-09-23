package com.bpao.devfoliobuilderapi.application.port.out.portfolio;

import com.bpao.devfoliobuilderapi.domain.model.PortfolioBlock;
import reactor.core.publisher.Mono;

/**
 * Puerto de salida: persistencia de bloques de portfolio.
 */
public interface PortfolioBlockPersistencePort {

    Mono<PortfolioBlock> save(PortfolioBlock portfolioBlock);
}