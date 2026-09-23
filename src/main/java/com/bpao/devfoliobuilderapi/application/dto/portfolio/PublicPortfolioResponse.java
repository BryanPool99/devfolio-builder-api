package com.bpao.devfoliobuilderapi.application.dto.portfolio;

import com.bpao.devfoliobuilderapi.application.dto.common.PageResponse;
import com.bpao.devfoliobuilderapi.application.dto.project.ProjectResponse;

import java.util.List;

/**
 * Respuesta publica de un portafolio por username: datos + bloques + proyectos
 * (los proyectos vienen paginados por offset/limit).
 */
public record PublicPortfolioResponse(
        String username,
        String title,
        boolean published,
        List<BlockResponse> blocks,
        PageResponse<ProjectResponse> projects) {
}