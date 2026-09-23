package com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.r2dbc;

import com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.entity.ProjectEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SpringDataProjectRepository extends ReactiveCrudRepository<ProjectEntity, Long> {

    @Query("SELECT * FROM projects WHERE portfolio_id = :portfolioId ORDER BY id")
    Flux<ProjectEntity> findByPortfolioId(Long portfolioId);

    @Query("SELECT * FROM projects WHERE portfolio_id = :portfolioId ORDER BY id LIMIT :limit OFFSET :offset")
    Flux<ProjectEntity> findPageByPortfolioId(Long portfolioId, int limit, long offset);

    Mono<Long> countByPortfolioId(Long portfolioId);
}