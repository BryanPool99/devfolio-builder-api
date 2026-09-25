package com.bpao.devfoliobuilderapi.application.usecase.auth;

import com.bpao.devfoliobuilderapi.application.dto.auth.AuthRequest;
import com.bpao.devfoliobuilderapi.application.port.out.security.PasswordEncoderPort;
import com.bpao.devfoliobuilderapi.application.port.out.security.TokenIssuerPort;
import com.bpao.devfoliobuilderapi.application.port.out.user.UserPersistencePort;
import com.bpao.devfoliobuilderapi.domain.exception.DomainException;
import com.bpao.devfoliobuilderapi.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticateUserServiceTest {

    private static final String PASSWORD = "secreto123";
    private static final String HASH = "hash-bcrypt";

    @Mock
    private UserPersistencePort userPersistence;
    @Mock
    private PasswordEncoderPort passwordEncoder;
    @Mock
    private TokenIssuerPort tokenIssuer;

    @InjectMocks
    private AuthenticateUserService service;

    private User user() {
        return User.builder()
                .id(1L).authId("auth-123").username("juanperez").email("juan@correo.com")
                .passwordHash(HASH).build();
    }

    @Test
    void autenticaConEmail() {
        when(userPersistence.findByEmail("juan@correo.com")).thenReturn(Mono.just(user()));
        when(passwordEncoder.matches(PASSWORD, HASH)).thenReturn(true);
        when(tokenIssuer.issue(any(User.class))).thenReturn(new TokenIssuerPort.IssuedToken("jwt", 900));

        StepVerifier.create(service.authenticate(new AuthRequest("Juan@Correo.com", PASSWORD)))
                .assertNext(response -> {
                    assertThat(response.accessToken()).isEqualTo("jwt");
                    assertThat(response.user().email()).isEqualTo("juan@correo.com");
                })
                .verifyComplete();

        verify(userPersistence, never()).findByUsername(any());
    }

    @Test
    void autenticaConUsernameSiElEmailNoExiste() {
        when(userPersistence.findByEmail("juanperez")).thenReturn(Mono.empty());
        when(userPersistence.findByUsername("juanperez")).thenReturn(Mono.just(user()));
        when(passwordEncoder.matches(PASSWORD, HASH)).thenReturn(true);
        when(tokenIssuer.issue(any(User.class))).thenReturn(new TokenIssuerPort.IssuedToken("jwt", 900));

        StepVerifier.create(service.authenticate(new AuthRequest("juanperez", PASSWORD)))
                .assertNext(response -> assertThat(response.accessToken()).isEqualTo("jwt"))
                .verifyComplete();
    }

    @Test
    void passwordIncorrectoDevuelveCredencialesInvalidas() {
        when(userPersistence.findByEmail("juan@correo.com")).thenReturn(Mono.just(user()));
        when(passwordEncoder.matches(PASSWORD, HASH)).thenReturn(false);

        StepVerifier.create(service.authenticate(new AuthRequest("juan@correo.com", PASSWORD)))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(DomainException.class);
                    assertThat(ex.getMessage()).isEqualTo("Credenciales invalidas");
                })
                .verify();

        verify(tokenIssuer, never()).issue(any());
    }

    @Test
    void usuarioInexistenteDevuelveElMismoErrorQuePasswordIncorrecto() {
        when(userPersistence.findByEmail(any())).thenReturn(Mono.empty());
        when(userPersistence.findByUsername(any())).thenReturn(Mono.empty());

        StepVerifier.create(service.authenticate(new AuthRequest("nadie@correo.com", PASSWORD)))
                .expectErrorSatisfies(ex -> assertThat(ex.getMessage()).isEqualTo("Credenciales invalidas"))
                .verify();

        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void usuarioSinHashNoPuedeAutenticarse() {
        User sinPassword = User.builder()
                .id(1L).authId("auth-123").username("juanperez").email("juan@correo.com").build();
        when(userPersistence.findByEmail("juan@correo.com")).thenReturn(Mono.just(sinPassword));

        StepVerifier.create(service.authenticate(new AuthRequest("juan@correo.com", PASSWORD)))
                .expectError(DomainException.class)
                .verify();

        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void identifierVacioDevuelveErrorDeValidacion() {
        StepVerifier.create(service.authenticate(new AuthRequest("  ", PASSWORD)))
                .expectError(DomainException.class)
                .verify();

        verify(userPersistence, never()).findByEmail(any());
    }

    @Test
    void passwordVaciaDevuelveErrorDeValidacion() {
        StepVerifier.create(service.authenticate(new AuthRequest("juan@correo.com", "")))
                .expectError(DomainException.class)
                .verify();

        verify(userPersistence, never()).findByEmail(any());
    }
}
