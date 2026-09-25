package com.bpao.devfoliobuilderapi.application.port.in.auth;

import com.bpao.devfoliobuilderapi.application.dto.auth.AuthRequest;
import com.bpao.devfoliobuilderapi.application.dto.auth.TokenResponse;
import reactor.core.publisher.Mono;

/**
 * Autenticacion por credenciales propias. El identificador puede ser email o username.
 */
public interface AuthenticateUserUseCase {

    Mono<TokenResponse> authenticate(AuthRequest request);
}
