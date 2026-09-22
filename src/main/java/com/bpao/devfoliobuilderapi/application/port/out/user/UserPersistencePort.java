package com.bpao.devfoliobuilderapi.application.port.out.user;

import com.bpao.devfoliobuilderapi.domain.model.User;
import reactor.core.publisher.Mono;

/**
 * Puerto de salida: persistencia de usuarios implementada por la infraestructura.
 */
public interface UserPersistencePort {

    Mono<User> findByAuthId(String authId);

    Mono<User> save(User user);
}