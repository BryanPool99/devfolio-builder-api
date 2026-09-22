package com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.entity;

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
    private String settings;
    private Instant createdAt;
}