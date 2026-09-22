package com.bpao.devfoliobuilderapi.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Entidad de dominio para la tabla categories.
 */
@Getter
@Builder
@AllArgsConstructor
public class Category {

    private final Long id;
    private final String name;
}