package com.bpao.devfoliobuilderapi.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * Entidad de dominio para la tabla portfolio_blocks.
 * El settings es JSON crudo (texto) hasta que se introduzca un conversor JSONB.
 */
@Getter
@Builder
@AllArgsConstructor
public class PortfolioBlock {

    private final Long id;
    private final Long portfolioId;
    private final String type;
    private final int position;
    private final String settings;
    private final Instant createdAt;
}