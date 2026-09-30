package com.bpao.devfoliobuilderapi.application.dto.project;

import java.util.List;

/**
 * Cuerpo para crear/actualizar un proyecto. technologyIds referencia el catalogo global.
 * visible es opcional: si no llega (nulo) el proyecto queda publicado por defecto.
 */
public record ProjectRequest(
        String title,
        String description,
        String repositoryUrl,
        String liveDemoUrl,
        String imageUrl,
        List<Long> technologyIds,
        Boolean visible) {

    public boolean visibleOrDefault() {
        return visible == null || visible;
    }
}