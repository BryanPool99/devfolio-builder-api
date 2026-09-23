package com.bpao.devfoliobuilderapi.application.port.out.catalog;

import com.bpao.devfoliobuilderapi.application.dto.catalog.TechnologyCatalogItem;
import com.bpao.devfoliobuilderapi.domain.model.Category;
import com.bpao.devfoliobuilderapi.domain.model.Technology;
import reactor.core.publisher.Flux;

import java.util.Collection;

/**
 * Puerto de salida: acceso al catalogo global (categorias y tecnologias).
 */
public interface CatalogPersistencePort {

    Flux<Category> findCategories();

    Flux<TechnologyCatalogItem> findTechnologies();

    Flux<TechnologyCatalogItem> findTechnologiesByCategory(Long categoryId);

    Flux<Technology> findByIds(Collection<Long> ids);
}