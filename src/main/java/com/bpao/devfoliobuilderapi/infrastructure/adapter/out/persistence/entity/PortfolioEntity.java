package com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("portfolios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioEntity {

    @Id
    private Long id;
    private Long userId;
    private String title;
    private Boolean isPublished;
    private Instant createdAt;
    private Instant updatedAt;
}