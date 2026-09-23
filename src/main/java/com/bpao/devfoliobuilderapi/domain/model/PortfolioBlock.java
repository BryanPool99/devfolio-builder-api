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

    public static final String TYPE_HERO = "HERO";
    public static final int DEFAULT_POSITION = 0;
    public static final String EMPTY_SETTINGS = "{}";

    private final Long id;
    private final Long portfolioId;
    private final String type;
    private final int position;
    private final String settings;
    private final Instant createdAt;

    public static PortfolioBlock createHero(Long portfolioId) {
        return builder()
                .portfolioId(portfolioId)
                .type(TYPE_HERO)
                .position(DEFAULT_POSITION)
                .settings(EMPTY_SETTINGS)
                .build();
    }
}