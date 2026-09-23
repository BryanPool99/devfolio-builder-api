package com.bpao.devfoliobuilderapi.application.port.out.project;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

/**
 * Puerto de salida: asociacion N:M entre proyectos y tecnologias.
 */
public interface ProjectTechnologyPersistencePort {

    Mono<Void> saveAll(Long projectId, Collection<Long> technologyIds);

    Mono<Void> deleteByProjectId(Long projectId);

    Flux<Long> findTechnologyIdsByProjectId(Long projectId);
}