package com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Tabla intermedia project_technologies (clave compuesta).
 */
@Table("project_technologies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectTechnologyEntity {

    private Long projectId;
    private Long technologyId;
}