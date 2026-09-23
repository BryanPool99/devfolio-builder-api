package com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.r2dbc;

import com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.entity.CategoryEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface SpringDataCategoryRepository extends ReactiveCrudRepository<CategoryEntity, Long> {
}