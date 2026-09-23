package com.bpao.devfoliobuilderapi.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 401 en JSON (ProblemDetail) para endpoints autenticados sin token valido.
 */
@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    private final RestProblemWriter problemWriter;

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        String detail = ex.getMessage() != null ? ex.getMessage() : "Autenticacion requerida";
        return problemWriter.write(exchange, HttpStatus.UNAUTHORIZED, detail);
    }
}