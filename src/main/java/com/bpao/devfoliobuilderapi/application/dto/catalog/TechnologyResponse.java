package com.bpao.devfoliobuilderapi.application.dto.catalog;

/**
 * Respuesta de la API para el recurso Technology.
 */
public record TechnologyResponse(Long id, String name, String iconUrl, Long categoryId, String categoryName) {
}