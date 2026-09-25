package com.bpao.devfoliobuilderapi.application.port.in.auth;

import com.bpao.devfoliobuilderapi.application.dto.auth.RegisterRequest;
import com.bpao.devfoliobuilderapi.application.dto.auth.TokenResponse;
import com.bpao.devfoliobuilderapi.domain.model.User;
import reactor.core.publisher.Mono;

/**
 * Alta de usuario con credenciales propias: valida disponibilidad de email y
 * username, cifra la contraseña y devuelve el token de acceso inicial.
 */
public interface RegisterUserUseCase {

    Mono<TokenResponse> register(RegisterRequest request);

    /**
     * Variante que devuelve solo el usuario persistido, sin emitir token.
     */
    Mono<User> registerWithoutToken(RegisterRequest request);
}
