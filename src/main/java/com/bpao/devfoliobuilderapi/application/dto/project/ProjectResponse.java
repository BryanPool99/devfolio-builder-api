package com.bpao.devfoliobuilderapi.application.dto.project;

import java.time.Instant;
import java.util.List;

/**
 * Respuesta de la API para el recurso Project.
 */
public record ProjectResponse(
        Long id,
        String title,
        String description,
        String repositoryUrl,
        String liveDemoUrl,
        String imageUrl,
        Instant createdAt,
        List<TechnologySummary> technologies) {
}