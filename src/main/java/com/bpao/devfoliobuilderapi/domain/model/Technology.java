package com.bpao.devfoliobuilderapi.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Entidad de dominio para la tabla technologies.
 */
@Getter
@Builder
@AllArgsConstructor
public class Technology {

    private final Long id;
    private final String name;
    private final String iconUrl;
    private final Long categoryId;
}