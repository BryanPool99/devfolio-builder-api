package com.bpao.devfoliobuilderapi.application.dto.auth;

/**
 * Cuerpo para POST /api/v1/auth/login. Se acepta email o username en el mismo campo.
 */
public record AuthRequest(
        String identifier,
        String password) {
}
