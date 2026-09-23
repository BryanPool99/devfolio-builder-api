package com.bpao.devfoliobuilderapi.application.port.out.project;

import com.bpao.devfoliobuilderapi.domain.model.Project;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Puerto de salida: persistencia de proyectos.
 */
public interface ProjectPersistencePort {

    Mono<Project> save(Project project);

    Mono<Project> findById(Long id);

    Flux<Project> findByPortfolioId(Long portfolioId);

    Flux<Project> findPageByPortfolioId(Long portfolioId, int limit, long offset);

    Mono<Long> countByPortfolioId(Long portfolioId);

    Mono<Void> deleteById(Long id);
}