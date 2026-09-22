package com.bpao.devfoliobuilderapi.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * Entidad de dominio para la tabla projects.
 */
@Getter
@Builder
@AllArgsConstructor
public class Project {

    private final Long id;
    private final Long portfolioId;
    private final String title;
    private final String description;
    private final String repositoryUrl;
    private final String liveDemoUrl;
    private final String imageUrl;
    private final Instant createdAt;
}