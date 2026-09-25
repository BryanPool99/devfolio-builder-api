package com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.r2dbc;

import com.bpao.devfoliobuilderapi.infrastructure.adapter.out.persistence.entity.UserEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface SpringDataUserRepository extends ReactiveCrudRepository<UserEntity, Long> {

    Mono<UserEntity> findByAuthId(String authId);

    Mono<UserEntity> findByUsername(String username);

    Mono<UserEntity> findByEmailIgnoreCase(String email);
}