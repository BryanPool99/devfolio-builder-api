package com.bpao.devfoliobuilderapi.application.port.in.catalog;

import com.bpao.devfoliobuilderapi.application.dto.catalog.CategoryResponse;
import reactor.core.publisher.Flux;

/**
 * Puerto de entrada: consulta del catalogo de categorias.
 */
public interface GetCategoriesUseCase {

    Flux<CategoryResponse> getCategories();
}