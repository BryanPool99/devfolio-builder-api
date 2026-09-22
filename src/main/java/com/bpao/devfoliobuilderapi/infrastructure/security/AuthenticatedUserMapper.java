package com.bpao.devfoliobuilderapi.infrastructure.security;

import com.bpao.devfoliobuilderapi.application.dto.AuthenticatedUser;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Traduccion entre el JWT de Neon Auth y el principal del hexagono.
 */
public final class AuthenticatedUserMapper {

    private AuthenticatedUserMapper() {
    }

    public static AuthenticatedUser from(Jwt jwt) {
        return new AuthenticatedUser(
                jwt.getSubject(),
                jwt.getClaimAsString("email"),
                jwt.getClaimAsString("preferred_username"));
    }
}