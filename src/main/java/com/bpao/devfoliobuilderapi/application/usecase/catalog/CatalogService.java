package com.bpao.devfoliobuilderapi.application.usecase.catalog;

import com.bpao.devfoliobuilderapi.application.dto.catalog.CategoryResponse;
import com.bpao.devfoliobuilderapi.application.dto.catalog.TechnologyCatalogItem;
import com.bpao.devfoliobuilderapi.application.dto.catalog.TechnologyResponse;
import com.bpao.devfoliobuilderapi.application.port.in.catalog.GetCategoriesUseCase;
import com.bpao.devfoliobuilderapi.application.port.in.catalog.GetTechnologiesUseCase;
import com.bpao.devfoliobuilderapi.application.port.out.catalog.CatalogPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * Caso de uso: consulta del catalogo global de categorias y tecnologias.
 */
@Service
@RequiredArgsConstructor
public class CatalogService implements GetCategoriesUseCase, GetTechnologiesUseCase {

    private final CatalogPersistencePort catalogPersistence;

    @Override
    public Flux<CategoryResponse> getCategories() {
        return catalogPersistence.findCategories()
                .map(category -> new CategoryResponse(category.getId(), category.getName()));
    }

    @Override
    public Flux<TechnologyResponse> getTechnologies(Long categoryId) {
        Flux<TechnologyCatalogItem> items = categoryId == null
                ? catalogPersistence.findTechnologies()
                : catalogPersistence.findTechnologiesByCategory(categoryId);
        return items.map(item -> new TechnologyResponse(
                item.id(), item.name(), item.iconUrl(), item.categoryId(), item.categoryName()));
    }
}