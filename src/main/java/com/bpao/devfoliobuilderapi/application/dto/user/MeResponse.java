package com.bpao.devfoliobuilderapi.application.dto.user;

import com.bpao.devfoliobuilderapi.application.dto.portfolio.PortfolioResponse;

/**
 * Respuesta de GET /api/v1/users/me tras la auto-sincronizacion.
 */
public record MeResponse(UserResponse user, PortfolioResponse portfolio) {
}