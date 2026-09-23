package com.bpao.devfoliobuilderapi.infrastructure.config;

import com.bpao.devfoliobuilderapi.infrastructure.security.RestAccessDeniedHandler;
import com.bpao.devfoliobuilderapi.infrastructure.security.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.net.URI;
import java.net.URL;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            RestAuthenticationEntryPoint authenticationEntryPoint,
            RestAccessDeniedHandler accessDeniedHandler,
            CorsConfigurationSource corsConfigurationSource) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.GET, "/api/v1/public/**").permitAll()
                        .pathMatchers(
                                "/actuator/health/**",
                                "/actuator/info",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**").permitAll()
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }

    /**
     * Decoder explicito: Neon Auth no publica OIDC discovery, asi que se usa
     * el JWK Set directamente. El validador de issuer es tolerante con la barra
     * final y acepta tanto el origin como el path con /auth.
     */
    @Bean
    ReactiveJwtDecoder jwtDecoder(
            @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String jwkSetUri,
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri) {
        NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefault(), tolerantIssuerValidator(issuerUri)));
        return decoder;
    }

    private OAuth2TokenValidator<Jwt> tolerantIssuerValidator(String issuerUri) {
        if (issuerUri == null || issuerUri.isBlank()) {
            return JwtValidators.createDefault();
        }
        return token -> {
            URL issuer = token.getIssuer();
            if (issuer != null && issuerMatches(issuerUri, issuer.toString())) {
                return OAuth2TokenValidatorResult.success();
            }
            String detail = "Invalid issuer: expected " + issuerUri + ", got " + issuer;
            OAuth2Error error = new OAuth2Error("invalid_issuer", detail, null);
            return OAuth2TokenValidatorResult.failure(error);
        };
    }

    private boolean issuerMatches(String configured, String actual) {
        String expected = stripTrailingSlash(configured);
        String candidate = stripTrailingSlash(actual);
        if (expected.equals(candidate)) {
            return true;
        }
        URI actualUri = URI.create(actual);
        String actualOrigin = actualUri.getScheme() + "://" + actualUri.getAuthority();
        if (stripTrailingSlash(actualOrigin).equals(expected)) {
            return true;
        }
        URI expectedUri = URI.create(configured);
        String expectedOrigin = expectedUri.getScheme() + "://" + expectedUri.getAuthority();
        return candidate.equals(stripTrailingSlash(expectedOrigin));
    }

    private String stripTrailingSlash(String value) {
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins:http://localhost:5173}") List<String> allowedOrigins) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}