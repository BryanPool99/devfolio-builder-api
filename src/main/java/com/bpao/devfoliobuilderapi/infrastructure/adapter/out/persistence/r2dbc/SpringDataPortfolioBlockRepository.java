package com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.r2dbc;

import com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.entity.PortfolioBlockEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface SpringDataPortfolioBlockRepository extends ReactiveCrudRepository<PortfolioBlockEntity, Long> {
}