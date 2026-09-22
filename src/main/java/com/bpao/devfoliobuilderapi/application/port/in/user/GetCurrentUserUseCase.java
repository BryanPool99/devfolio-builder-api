package com.bpao.devfoliobuilderapi.application.port.in.user;

import com.bpao.devfoliobuilderapi.application.dto.AuthenticatedUser;
import com.bpao.devfoliobuilderapi.domain.model.User;
import reactor.core.publisher.Mono;

/**
 * Puerto de entrada: consulta del usuario a partir del principal autenticado.
 */
public interface GetCurrentUserUseCase {

    Mono<User> findOrCreate(AuthenticatedUser authenticatedUser);
}