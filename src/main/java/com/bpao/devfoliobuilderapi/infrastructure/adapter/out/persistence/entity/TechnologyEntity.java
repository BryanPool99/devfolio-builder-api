package com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("technologies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechnologyEntity {

    @Id
    private Long id;
    private String name;
    private String iconUrl;
    private Long categoryId;
}