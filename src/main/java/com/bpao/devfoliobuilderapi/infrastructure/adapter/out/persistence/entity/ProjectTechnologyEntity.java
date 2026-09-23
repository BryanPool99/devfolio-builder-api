package com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Tabla intermedia project_technologies. Spring Data R2DBC exige un identificador
 * unico: se usa un id sintetico (BIGSERIAL) ademas de la clave compuesta del esquema.
 */
@Table("project_technologies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectTechnologyEntity {

    @Id
    private Long id;
    private Long projectId;
    private Long technologyId;
}