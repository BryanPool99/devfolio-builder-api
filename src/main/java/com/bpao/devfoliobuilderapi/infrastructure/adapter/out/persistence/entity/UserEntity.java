package com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.entity;

import com.bpao.devfoliobuilderapi.domain.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    private Long id;
    private String authId;
    private String username;
    private String email;
    private String passwordHash;
    @Builder.Default
    private UserRole role = UserRole.USER;
    private Boolean emailVerified;
    private Instant createdAt;
    private Instant updatedAt;
}
