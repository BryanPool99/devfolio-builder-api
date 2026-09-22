package com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("projects")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectEntity {

    @Id
    private Long id;
    private Long portfolioId;
    private String title;
    private String description;
    private String repositoryUrl;
    private String liveDemoUrl;
    private String imageUrl;
    private Instant createdAt;
}