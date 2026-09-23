package com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.r2dbc;

import com.bpao.devfoliobuilderapi.application.port.out.project.ProjectPersistencePort;
import com.bpao.devfoliobuilderapi.domain.model.Project;
import com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.entity.ProjectEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Adaptador de salida: traduce entre el hexagono (Project) y Spring Data R2DBC.
 */
@Component
@RequiredArgsConstructor
public class ProjectPersistenceAdapter implements ProjectPersistencePort {

    private final SpringDataProjectRepository repository;

    @Override
    public Mono<Project> save(Project project) {
        return repository.save(toEntity(project)).map(this::toDomain);
    }

    @Override
    public Mono<Project> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Flux<Project> findByPortfolioId(Long portfolioId) {
        return repository.findByPortfolioId(portfolioId).map(this::toDomain);
    }

    @Override
    public Flux<Project> findPageByPortfolioId(Long portfolioId, int limit, long offset) {
        return repository.findPageByPortfolioId(portfolioId, limit, offset).map(this::toDomain);
    }

    @Override
    public Mono<Long> countByPortfolioId(Long portfolioId) {
        return repository.countByPortfolioId(portfolioId);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id);
    }

    private ProjectEntity toEntity(Project project) {
        return ProjectEntity.builder()
                .id(project.getId())
                .portfolioId(project.getPortfolioId())
                .title(project.getTitle())
                .description(project.getDescription())
                .repositoryUrl(project.getRepositoryUrl())
                .liveDemoUrl(project.getLiveDemoUrl())
                .imageUrl(project.getImageUrl())
                .createdAt(project.getCreatedAt())
                .build();
    }

    private Project toDomain(ProjectEntity entity) {
        return Project.builder()
                .id(entity.getId())
                .portfolioId(entity.getPortfolioId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .repositoryUrl(entity.getRepositoryUrl())
                .liveDemoUrl(entity.getLiveDemoUrl())
                .imageUrl(entity.getImageUrl())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}