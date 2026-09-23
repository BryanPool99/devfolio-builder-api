package com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.r2dbc;

import com.bpao.devfoliobuilderapi.application.port.out.portfolio.PortfolioPersistencePort;
import com.bpao.devfoliobuilderapi.domain.model.Portfolio;
import com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.entity.PortfolioEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Adaptador de salida: traduce entre el hexagono (Portfolio) y Spring Data R2DBC.
 */
@Component
@RequiredArgsConstructor
public class PortfolioPersistenceAdapter implements PortfolioPersistencePort {

    private final SpringDataPortfolioRepository repository;

    @Override
    public Mono<Portfolio> findByUserId(Long userId) {
        return repository.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public Mono<Portfolio> save(Portfolio portfolio) {
        return repository.save(toEntity(portfolio)).map(this::toDomain);
    }

    private PortfolioEntity toEntity(Portfolio portfolio) {
        return PortfolioEntity.builder()
                .id(portfolio.getId())
                .userId(portfolio.getUserId())
                .title(portfolio.getTitle())
                .isPublished(portfolio.isPublished())
                .createdAt(portfolio.getCreatedAt())
                .updatedAt(portfolio.getUpdatedAt())
                .build();
    }

    private Portfolio toDomain(PortfolioEntity entity) {
        return Portfolio.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .title(entity.getTitle())
                .published(Boolean.TRUE.equals(entity.getIsPublished()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}