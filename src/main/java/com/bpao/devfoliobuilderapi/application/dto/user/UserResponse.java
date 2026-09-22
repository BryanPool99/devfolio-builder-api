package com.bpao.devfoliobuilderapi.application.dto.user;

import java.time.Instant;

/**
 * Respuesta de la API para el recurso User.
 */
public record UserResponse(Long id, String username, String email, Instant createdAt) {
}