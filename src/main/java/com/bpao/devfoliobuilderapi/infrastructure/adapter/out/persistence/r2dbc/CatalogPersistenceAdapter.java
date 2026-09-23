package com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.r2dbc;

import com.bpao.devfoliobuilderapi.application.dto.catalog.TechnologyCatalogItem;
import com.bpao.devfoliobuilderapi.application.port.out.catalog.CatalogPersistencePort;
import com.bpao.devfoliobuilderapi.domain.model.Category;
import com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.entity.CategoryEntity;
import com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.entity.TechnologyEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Adaptador de salida: catalogo global. Se usan unicamente repositorios
 * Spring Data R2DBC; el nombre de la categoria se resuelve con una consulta
 * separada sobre categories (sin JOIN manual).
 */
@Component
@RequiredArgsConstructor
public class CatalogPersistenceAdapter implements CatalogPersistencePort {

    private final SpringDataCategoryRepository categoryRepository;
    private final SpringDataTechnologyRepository technologyRepository;

    @Override
    public Flux<Category> findCategories() {
        return categoryRepository.findAll()
                .map(entity -> new Category(entity.getId(), entity.getName()));
    }

    @Override
    public Flux<TechnologyCatalogItem> findTechnologies() {
        return categoryNames()
                .flatMapMany(names -> technologyRepository.findAllOrderedByName()
                        .map(entity -> toCatalogItem(entity, names.get(entity.getCategoryId()))));
    }

    @Override
    public Flux<TechnologyCatalogItem> findTechnologiesByCategory(Long categoryId) {
        return categoryNames()
                .flatMapMany(names -> technologyRepository.findByCategoryIdOrderedByName(categoryId)
                        .map(entity -> toCatalogItem(entity, names.get(entity.getCategoryId()))));
    }

    private Mono<Map<Long, String>> categoryNames() {
        return categoryRepository.findAll()
                .collectMap(CategoryEntity::getId, CategoryEntity::getName);
    }

    private TechnologyCatalogItem toCatalogItem(TechnologyEntity entity, String categoryName) {
        return new TechnologyCatalogItem(
                entity.getId(),
                entity.getName(),
                entity.getIconUrl(),
                entity.getCategoryId(),
                categoryName);
    }
}