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

    public static final String DEFAULT_TITLE = "Mi Portafolio";

    private final Long id;
    private final Long userId;
    private final String title;
    private final boolean published;
    private final Instant createdAt;
    private final Instant updatedAt;

    public static Portfolio createDefault(Long userId) {
        return builder()
                .userId(userId)
                .title(DEFAULT_TITLE)
                .published(true)
                .build();
    }
}