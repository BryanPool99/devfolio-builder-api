package com.bpao.devfoliobuilderapi.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configuracion del JWT que emite el propio backend.
 * <p>
 * Se firma con un unico secreto compartido (HS256) porque el mismo servicio
 * emite y valida: no hay un segundo consumidor que necesite una llave publica.
 */
@ConfigurationProperties(prefix = "app.security.jwt")
public record JwtProperties(
        String secret,
        String issuer,
        Duration accessTokenTtl) {

    public static final String DEFAULT_ISSUER = "https://devfolio-builder.api";
    public static final Duration DEFAULT_TTL = Duration.ofMinutes(15);
    private static final int MIN_SECRET_BYTES = 32;

    public JwtProperties {
        secret = secret == null ? "" : secret.trim();
        if (secret.length() < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                    "JWT_SECRET debe tener al menos " + MIN_SECRET_BYTES + " caracteres (longitud actual: "
                            + secret.length() + "). En local hay un valor por defecto; en produccion define JWT_SECRET.");
        }
        if (issuer == null || issuer.isBlank()) {
            issuer = DEFAULT_ISSUER;
        }
        if (accessTokenTtl == null || accessTokenTtl.isNegative() || accessTokenTtl.isZero()) {
            accessTokenTtl = DEFAULT_TTL;
        }
    }
}
