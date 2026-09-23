package com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.r2dbc;

import com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.entity.PortfolioBlockEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SpringDataPortfolioBlockRepository extends ReactiveCrudRepository<PortfolioBlockEntity, Long> {

    @Query("SELECT * FROM portfolio_blocks WHERE portfolio_id = :portfolioId ORDER BY position")
    Flux<PortfolioBlockEntity> findByPortfolioId(Long portfolioId);

    @Modifying
    @Query("DELETE FROM portfolio_blocks WHERE portfolio_id = :portfolioId")
    Mono<Void> deleteByPortfolioId(Long portfolioId);
}