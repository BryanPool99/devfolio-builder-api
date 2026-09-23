package com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.r2dbc;

import com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.entity.ProjectTechnologyEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SpringDataProjectTechnologyRepository extends ReactiveCrudRepository<ProjectTechnologyEntity, Long> {

    @Query("SELECT * FROM project_technologies WHERE project_id = :projectId ORDER BY technology_id")
    Flux<ProjectTechnologyEntity> findByProjectId(Long projectId);

    @Modifying
    @Query("DELETE FROM project_technologies WHERE project_id = :projectId")
    Mono<Void> deleteByProjectId(Long projectId);
}