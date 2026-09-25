package com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.r2dbc;

import com.bpao.devfoliobuilderapi.application.port.out.user.UserPersistencePort;
import com.bpao.devfoliobuilderapi.domain.model.User;
import com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Adaptador de salida: traduce entre el hexagono (User) y Spring Data R2DBC (UserEntity).
 */
@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPersistencePort {

    private final SpringDataUserRepository repository;

    @Override
    public Mono<User> findByAuthId(String authId) {
        return repository.findByAuthId(authId).map(this::toDomain);
    }

    @Override
    public Mono<User> findByUsername(String username) {
        return repository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return repository.findByEmailIgnoreCase(email).map(this::toDomain);
    }

    @Override
    public Mono<User> save(User user) {
        return repository.save(toEntity(user))
                .flatMap(saved -> repository.findByAuthId(saved.getAuthId()))
                .map(this::toDomain);
    }

    private UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .authId(user.getAuthId())
                .username(user.getUsername())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .role(user.getRole())
                .emailVerified(user.isEmailVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .authId(entity.getAuthId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .passwordHash(entity.getPasswordHash())
                .role(entity.getRole())
                .emailVerified(Boolean.TRUE.equals(entity.getEmailVerified()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
