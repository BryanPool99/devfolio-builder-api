package com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.r2dbc;

import com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.entity.TechnologyEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface SpringDataTechnologyRepository extends ReactiveCrudRepository<TechnologyEntity, Long> {

    @Query("SELECT * FROM technologies ORDER BY name")
    Flux<TechnologyEntity> findAllOrderedByName();

    @Query("SELECT * FROM technologies WHERE category_id = :categoryId ORDER BY name")
    Flux<TechnologyEntity> findByCategoryIdOrderedByName(Long categoryId);
}