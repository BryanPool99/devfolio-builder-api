package com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.r2dbc;

import com.bpao.devfoliobuilderapi.application.port.out.project.ProjectTechnologyPersistencePort;
import com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.entity.ProjectTechnologyEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

/**
 * Adaptador de salida: asociacion N:M proyectos-tecnologias.
 */
@Component
@RequiredArgsConstructor
public class ProjectTechnologyPersistenceAdapter implements ProjectTechnologyPersistencePort {

    private final SpringDataProjectTechnologyRepository repository;

    @Override
    public Mono<Void> saveAll(Long projectId, Collection<Long> technologyIds) {
        Flux<ProjectTechnologyEntity> entities = Flux.fromIterable(technologyIds)
                .distinct()
                .map(technologyId -> ProjectTechnologyEntity.builder()
                        .projectId(projectId)
                        .technologyId(technologyId)
                        .build());
        return repository.saveAll(entities).then();
    }

    @Override
    public Mono<Void> deleteByProjectId(Long projectId) {
        return repository.deleteByProjectId(projectId);
    }

    @Override
    public Flux<Long> findTechnologyIdsByProjectId(Long projectId) {
        return repository.findByProjectId(projectId).map(ProjectTechnologyEntity::getTechnologyId);
    }
}