package com.bpao.devfoliobuilderapi.application.port.out.portfolio;

import com.bpao.devfoliobuilderapi.domain.model.PortfolioBlock;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Puerto de salida: persistencia de bloques de portfolio.
 */
public interface PortfolioBlockPersistencePort {

    Mono<PortfolioBlock> save(PortfolioBlock portfolioBlock);

    Flux<PortfolioBlock> saveAll(List<PortfolioBlock> portfolioBlocks);

    Flux<PortfolioBlock> findByPortfolioId(Long portfolioId);

    Mono<Void> deleteAllByPortfolioId(Long portfolioId);
}