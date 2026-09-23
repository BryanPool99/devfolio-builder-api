package com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.r2dbc;

import com.bpao.devfoliobuilderapi.application.port.out.portfolio.PortfolioBlockPersistencePort;
import com.bpao.devfoliobuilderapi.domain.model.PortfolioBlock;
import com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.entity.PortfolioBlockEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Adaptador de salida: traduce entre el hexagono (PortfolioBlock) y Spring Data R2DBC.
 */
@Component
@RequiredArgsConstructor
public class PortfolioBlockPersistenceAdapter implements PortfolioBlockPersistencePort {

    private final SpringDataPortfolioBlockRepository repository;

    @Override
    public Mono<PortfolioBlock> save(PortfolioBlock block) {
        return repository.save(toEntity(block)).map(this::toDomain);
    }

    private PortfolioBlockEntity toEntity(PortfolioBlock block) {
        return PortfolioBlockEntity.builder()
                .id(block.getId())
                .portfolioId(block.getPortfolioId())
                .type(block.getType())
                .position(block.getPosition())
                .settings(block.getSettings())
                .createdAt(block.getCreatedAt())
                .build();
    }

    private PortfolioBlock toDomain(PortfolioBlockEntity entity) {
        return PortfolioBlock.builder()
                .id(entity.getId())
                .portfolioId(entity.getPortfolioId())
                .type(entity.getType())
                .position(entity.getPosition())
                .settings(entity.getSettings())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}