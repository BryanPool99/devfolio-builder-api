package com.bpao.devfoliobuilderapi.infrastructure.adapter.in.web;

import com.bpao.devfoliobuilderapi.application.dto.auth.AuthRequest;
import com.bpao.devfoliobuilderapi.application.dto.auth.RegisterRequest;
import com.bpao.devfoliobuilderapi.application.dto.auth.TokenResponse;
import com.bpao.devfoliobuilderapi.application.dto.user.SyncResult;
import com.bpao.devfoliobuilderapi.application.dto.user.UserResponse;
import com.bpao.devfoliobuilderapi.application.port.in.auth.AuthenticateUserUseCase;
import com.bpao.devfoliobuilderapi.application.port.in.auth.RegisterUserUseCase;
import com.bpao.devfoliobuilderapi.application.port.in.user.SyncUserUseCase;
import com.bpao.devfoliobuilderapi.application.port.out.security.TokenIssuerPort;
import com.bpao.devfoliobuilderapi.domain.exception.ConflictException;
import com.bpao.devfoliobuilderapi.domain.exception.InvalidCredentialsException;
import com.bpao.devfoliobuilderapi.domain.model.Portfolio;
import com.bpao.devfoliobuilderapi.domain.model.User;
import com.bpao.devfoliobuilderapi.domain.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

/**
 * Cubre la capa HTTP de la autenticacion propia: que /auth/* sea publico, que el
 * resto exija Bearer, y que los claims del token propio lleguen al caso de uso
 * como principal.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthFlowWebTest {

    private static final String ISSUER = "https://devfolio-builder.webtest";

    @DynamicPropertySource
    static void jwtKeys(DynamicPropertyRegistry registry) {
        registry.add("app.security.jwt.issuer", () -> ISSUER);
    }

    @Autowired
    private TokenIssuerPort tokenIssuer;

    @LocalServerPort
    private int port;

    private WebTestClient client;

    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;
    @MockitoBean
    private AuthenticateUserUseCase authenticateUserUseCase;
    @MockitoBean
    private SyncUserUseCase syncUserUseCase;

    @BeforeEach
    void connect() {
        client = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    private String bearer() {
        return "Bearer " + tokenIssuer.issue(user()).value();
    }

    private User user() {
        return User.builder()
                .id(1L).authId("auth-123").username("juanperez").email("juan@correo.com")
                .passwordHash("hash").role(UserRole.USER).build();
    }

    @Test
    void registroDevuelve201ConTokenYUsuario() {
        when(registerUserUseCase.register(any(RegisterRequest.class)))
                .thenReturn(Mono.just(new TokenResponse(
                        "jwt.emitido", "Bearer", 900,
                        new UserResponse(1L, "juanperez", "juan@correo.com", Instant.now()))));

        client.post().uri("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new RegisterRequest("juanperez", "juan@correo.com", "secreto123"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.accessToken").isEqualTo("jwt.emitido")
                .jsonPath("$.tokenType").isEqualTo("Bearer")
                .jsonPath("$.expiresIn").isEqualTo(900)
                .jsonPath("$.user.email").isEqualTo("juan@correo.com");
    }

    @Test
    void loginDevuelve200ConToken() {
        when(authenticateUserUseCase.authenticate(any(AuthRequest.class)))
                .thenReturn(Mono.just(new TokenResponse(
                        "jwt.emitido", "Bearer", 900,
                        new UserResponse(1L, "juanperez", "juan@correo.com", Instant.now()))));

        client.post().uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new AuthRequest("juan@correo.com", "secreto123"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.accessToken").isEqualTo("jwt.emitido");
    }

    @Test
    void loginConCredencialesInvalidasDevuelve401() {
        when(authenticateUserUseCase.authenticate(any(AuthRequest.class)))
                .thenReturn(Mono.error(new InvalidCredentialsException("Credenciales invalidas")));

        client.post().uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new AuthRequest("juan@correo.com", "mala"))
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Credenciales invalidas");
    }

    @Test
    void emailDuplicadoDevuelve409() {
        when(registerUserUseCase.register(any(RegisterRequest.class)))
                .thenReturn(Mono.error(new ConflictException("El email ya esta registrado")));

        client.post().uri("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new RegisterRequest("juanperez", "juan@correo.com", "secreto123"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.detail").isEqualTo("El email ya esta registrado");
    }

    @Test
    void sinTokenElApiProtegidoDevuelve401() {
        client.get().uri("/api/v1/users/me")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void tokenInvalidoDevuelve401() {
        client.get().uri("/api/v1/users/me")
                .header("Authorization", "Bearer no.es.un.token")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void tokenPropioLlegaAlCasoDeUsoComoPrincipal() {
        Portfolio portfolio = Portfolio.builder()
                .id(10L).userId(1L).title(Portfolio.DEFAULT_TITLE).published(true).build();
        when(syncUserUseCase.sync(argThat(principal ->
                principal.authId().equals("auth-123")
                        && principal.email().equals("juan@correo.com")
                        && principal.preferredUsername().equals("juanperez"))))
                .thenReturn(Mono.just(new SyncResult(
                        user(), portfolio)));

        client.get().uri("/api/v1/users/me")
                .header("Authorization", bearer())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.user.email").isEqualTo("juan@correo.com")
                .jsonPath("$.portfolio.id").isEqualTo(10);
    }

    @Test
    void cuerpoInvalidoDevuelve400() {
        client.post().uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{ esto no es json ")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void sinBodyDevuelve400() {
        client.post().uri("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void metodoNoSoportadoDevuelve405() {
        client.get().uri("/api/v1/auth/login")
                .exchange()
                .expectStatus().isEqualTo(405);
    }

    @Test
    void rutaInexistenteProtegidaDevuelve401SinToken() {
        client.get().uri("/api/v1/ruta-que-no-existe")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
