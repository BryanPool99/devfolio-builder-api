package com.bpao.devfoliobuilderapi.infrastructure.config;

import com.bpao.devfoliobuilderapi.domain.model.User;
import com.bpao.devfoliobuilderapi.domain.model.UserRole;
import com.bpao.devfoliobuilderapi.infrastructure.security.JwtTokenIssuer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Firma con el secreto, validacion con el mismo secreto y rechazo de lo que no
 * corresponde. Vive en el mismo paquete que JwtConfig para invocar sus beans.
 */
class JwtConfigTest {

    private static final String ISSUER = "https://devfolio-builder.test";
    private static final String SECRET = "secreto-de-prueba-suficientemente-largo-1234567890";
    private static final Duration TTL = Duration.ofMinutes(15);

    private JwtProperties properties;
    private JwtTokenIssuer issuer;
    private ReactiveJwtDecoder decoder;

    private static JwtProperties props(String secret, String issuer, Duration ttl) {
        return new JwtProperties(secret, issuer, ttl);
    }

    @BeforeEach
    void setUp() {
        properties = props(SECRET, ISSUER, TTL);
        JwtConfig config = new JwtConfig();
        issuer = new JwtTokenIssuer(config.jwtEncoder(properties), properties);
        decoder = config.jwtDecoder(properties);
    }

    private User user() {
        return User.builder()
                .id(7L).authId("auth-123").username("juanperez").email("juan@correo.com")
                .passwordHash("hash").role(UserRole.USER).build();
    }

    @Test
    void tokenEmitidoContieneLosClaimsEsperados() {
        var issued = issuer.issue(user());

        assertThat(issued.value().split("\\.")).hasSize(3);
        assertThat(issued.expiresInSeconds()).isEqualTo(900);

        StepVerifier.create(decoder.decode(issued.value()))
                .assertNext(jwt -> {
                    assertThat(jwt.getSubject()).isEqualTo("auth-123");
                    assertThat(jwt.getIssuer().toString()).isEqualTo(ISSUER);
                    assertThat(jwt.getClaimAsString("email")).isEqualTo("juan@correo.com");
                    assertThat(jwt.getClaimAsString("preferred_username")).isEqualTo("juanperez");
                    assertThat(jwt.getClaimAsString("role")).isEqualTo("USER");
                })
                .verifyComplete();
    }

    @Test
    void headerDeclaraHs256() {
        String[] parts = issuer.issue(user()).value().split("\\.");
        String header = new String(Base64.getUrlDecoder().decode(parts[0]));

        assertThat(header).contains("\"HS256\"");
    }

    @Test
    void tokenDeOtroIssuerEsRechazado() {
        JwtProperties otroIssuer = props(SECRET, "https://otro-servidor.test", TTL);
        JwtTokenIssuer emisorExtranjero = new JwtTokenIssuer(
                new JwtConfig().jwtEncoder(otroIssuer), otroIssuer);

        StepVerifier.create(decoder.decode(emisorExtranjero.issue(user()).value()))
                .expectError()
                .verify();
    }

    @Test
    void tokenFirmadoConOtroSecretoEsRechazado() {
        JwtProperties otroSecreto = props(
                "otro-secreto-de-prueba-suficientemente-largo-9876543210", ISSUER, TTL);
        JwtTokenIssuer emisorExtranjero = new JwtTokenIssuer(
                new JwtConfig().jwtEncoder(otroSecreto), otroSecreto);

        StepVerifier.create(decoder.decode(emisorExtranjero.issue(user()).value()))
                .expectError()
                .verify();
    }

    @Test
    void elTtlConfigurableSeReflejaEnElToken() {
        JwtProperties unaHora = props(SECRET, ISSUER, Duration.ofHours(1));

        var issued = new JwtTokenIssuer(new JwtConfig().jwtEncoder(unaHora), unaHora).issue(user());

        assertThat(issued.expiresInSeconds()).isEqualTo(3600);
        StepVerifier.create(decoder.decode(issued.value()))
                .assertNext(jwt -> assertThat(jwt.getExpiresAt()).isAfter(jwt.getIssuedAt().plusSeconds(3000)))
                .verifyComplete();
    }

    @Test
    void unTtlInvalidoCaeAlDefaultDeQuinceMinutos() {
        assertThat(props(SECRET, ISSUER, Duration.ofMinutes(-5)).accessTokenTtl())
                .isEqualTo(Duration.ofMinutes(15));
    }

    @Test
    void secretoCortoFallaConMensajeExplicito() {
        assertThatThrownBy(() -> props("corto", ISSUER, TTL))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JWT_SECRET");
    }

    @Test
    void configuracionPorDefectoUsaQuinceMinutos() {
        JwtProperties porDefecto = props(SECRET, null, null);

        assertThat(porDefecto.issuer()).isEqualTo("https://devfolio-builder.api");
        assertThat(porDefecto.accessTokenTtl()).isEqualTo(Duration.ofMinutes(15));
    }
}
