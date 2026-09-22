package com.bpao.devfoliobuilderapi.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * Entidad de dominio para la tabla portfolios.
 * Un usuario tiene un unico portafolio.
 */
@Getter
@Builder
@AllArgsConstructor
public class Portfolio {

    private final Long id;
    private final Long userId;
    private final String title;
    private final boolean published;
    private final Instant createdAt;
    private final Instant updatedAt;
}