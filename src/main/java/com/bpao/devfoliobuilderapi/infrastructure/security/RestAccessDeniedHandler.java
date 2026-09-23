package com.bpao.devfoliobuilderapi.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 403 en JSON (ProblemDetail) para accesos denegados.
 */
@Component
@RequiredArgsConstructor
public class RestAccessDeniedHandler implements ServerAccessDeniedHandler {

    private final RestProblemWriter problemWriter;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        return problemWriter.write(exchange, HttpStatus.FORBIDDEN, "Acceso denegado");
    }
}