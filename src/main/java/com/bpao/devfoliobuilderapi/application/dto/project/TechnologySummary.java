package com.bpao.devfoliobuilderapi.application.dto.project;

/**
 * Informacion compacta de una tecnologia asociada a un proyecto.
 */
public record TechnologySummary(Long id, String name, String iconUrl) {
}