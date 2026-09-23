package com.bpao.devfoliobuilderapi.application.dto.project;

import java.util.List;

/**
 * Cuerpo para crear/actualizar un proyecto. technologyIds referencia el catalogo global.
 */
public record ProjectRequest(
        String title,
        String description,
        String repositoryUrl,
        String liveDemoUrl,
        String imageUrl,
        List<Long> technologyIds) {
}