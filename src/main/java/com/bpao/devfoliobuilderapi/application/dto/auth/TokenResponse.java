package com.bpao.devfoliobuilderapi.application.dto.auth;

import com.bpao.devfoliobuilderapi.application.dto.user.UserResponse;

/**
 * Respuesta de login y registro: token de acceso mas el usuario autenticado.
 */
public record TokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        UserResponse user) {
}
