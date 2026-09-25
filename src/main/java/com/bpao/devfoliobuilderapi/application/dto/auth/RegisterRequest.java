package com.bpao.devfoliobuilderapi.application.dto.auth;

/**
 * Cuerpo para POST /api/v1/auth/register.
 */
public record RegisterRequest(
        String username,
        String email,
        String password) {
}
