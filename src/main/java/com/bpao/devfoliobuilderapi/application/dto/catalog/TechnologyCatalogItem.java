package com.bpao.devfoliobuilderapi.application.dto.catalog;

/**
 * Item del catalogo de tecnologias: tecnologia + nombre de su categoria.
 */
public record TechnologyCatalogItem(Long id, String name, String iconUrl, Long categoryId, String categoryName) {
}