package com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.r2dbc;

import com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.entity.PortfolioEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface SpringDataPortfolioRepository extends ReactiveCrudRepository<PortfolioEntity, Long> {

    Mono<PortfolioEntity> findByUserId(Long userId);
}