package com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.r2dbc;

import com.bpao.devfoliobuilderapi.application.port.out.portfolio.PortfolioBlockPersistencePort;
import com.bpao.devfoliobuilderapi.domain.model.PortfolioBlock;
import com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.entity.PortfolioBlockEntity;
import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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

    @Override
    public Flux<PortfolioBlock> saveAll(List<PortfolioBlock> blocks) {
        return Flux.fromIterable(blocks)
                .map(this::toEntity)
                .as(repository::saveAll)
                .map(this::toDomain);
    }

    @Override
    public Flux<PortfolioBlock> findByPortfolioId(Long portfolioId) {
        return repository.findByPortfolioId(portfolioId).map(this::toDomain);
    }

    @Override
    public Mono<Void> deleteAllByPortfolioId(Long portfolioId) {
        return repository.deleteByPortfolioId(portfolioId);
    }

    private PortfolioBlockEntity toEntity(PortfolioBlock block) {
        return PortfolioBlockEntity.builder()
                .id(block.getId())
                .portfolioId(block.getPortfolioId())
                .type(block.getType())
                .position(block.getPosition())
                .settings(toJson(block.getSettings()))
                .createdAt(block.getCreatedAt())
                .build();
    }

    private PortfolioBlock toDomain(PortfolioBlockEntity entity) {
        return PortfolioBlock.builder()
                .id(entity.getId())
                .portfolioId(entity.getPortfolioId())
                .type(entity.getType())
                .position(entity.getPosition())
                .settings(fromJson(entity.getSettings()))
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private Json toJson(String settings) {
        return settings == null ? null : Json.of(settings);
    }

    private String fromJson(Json settings) {
        return settings == null ? null : settings.asString();
    }
}