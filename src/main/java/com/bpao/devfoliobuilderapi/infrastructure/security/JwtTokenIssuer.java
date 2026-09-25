package com.bpao.devfoliobuilderapi.infrastructure.security;

import com.bpao.devfoliobuilderapi.application.port.out.security.TokenIssuerPort;
import com.bpao.devfoliobuilderapi.domain.model.User;
import com.bpao.devfoliobuilderapi.domain.model.UserRole;
import com.bpao.devfoliobuilderapi.infrastructure.config.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Emite el token de acceso firmado con HS256.
 * Los claims se mantienen compatibles con lo que AuthenticatedUserMapper ya leia:
 * 'sub' como authId, 'email' y 'preferred_username'. Se agrega 'role' para
 * fases posteriores con permisos.
 */
@Component
@RequiredArgsConstructor
public class JwtTokenIssuer implements TokenIssuerPort {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties properties;

    @Override
    public IssuedToken issue(User user) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(properties.accessTokenTtl());

        UserRole role = user.getRole() == null ? UserRole.USER : user.getRole();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(properties.issuer())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(user.getAuthId())
                .claim("email", user.getEmail())
                .claim("preferred_username", user.getUsername())
                .claim("role", role.name())
                .build();

        // El encoder ya esta configurado con HS256, asi que basta con los claims.
        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return new IssuedToken(token, properties.accessTokenTtl().toSeconds());
    }
}
