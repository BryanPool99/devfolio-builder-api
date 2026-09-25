package com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.entity;

import io.r2dbc.postgresql.codec.Json;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("portfolio_blocks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioBlockEntity {

    @Id
    private Long id;
    private Long portfolioId;
    private String type;
    private Integer position;
    /**
     * Json y no String: la columna es jsonb y R2DBC no castea solo de varchar.
     * El adaptador convierte el texto del dominio al escribir y de vuelta al leer.
     */
    private Json settings;
    private Instant createdAt;
}