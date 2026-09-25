package com.bpao.devfoliobuilderapi.application.usecase.auth;

import com.bpao.devfoliobuilderapi.application.dto.auth.RegisterRequest;
import com.bpao.devfoliobuilderapi.application.dto.auth.TokenResponse;
import com.bpao.devfoliobuilderapi.application.port.out.security.PasswordEncoderPort;
import com.bpao.devfoliobuilderapi.application.port.out.security.TokenIssuerPort;
import com.bpao.devfoliobuilderapi.application.port.out.user.UserPersistencePort;
import com.bpao.devfoliobuilderapi.domain.exception.ConflictException;
import com.bpao.devfoliobuilderapi.domain.exception.DomainException;
import com.bpao.devfoliobuilderapi.domain.model.User;
import com.bpao.devfoliobuilderapi.domain.model.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserServiceTest {

    private static final String PASSWORD = "secreto123";

    @Mock
    private UserPersistencePort userPersistence;
    @Mock
    private PasswordEncoderPort passwordEncoder;
    @Mock
    private TokenIssuerPort tokenIssuer;

    @InjectMocks
    private RegisterUserService service;

    @Test
    void registraCifrandoLaContrasenaYEmitiendoToken() {
        when(userPersistence.findByEmail(anyString())).thenReturn(Mono.empty());
        when(userPersistence.findByUsername(anyString())).thenReturn(Mono.empty());
        when(passwordEncoder.hash(PASSWORD)).thenReturn("hash-bcrypt");
        when(userPersistence.save(any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(tokenIssuer.issue(any(User.class))).thenReturn(new TokenIssuerPort.IssuedToken("jwt.firmado", 900));

        StepVerifier.create(service.register(new RegisterRequest("juanperez", "Juan@Correo.com", PASSWORD)))
                .assertNext(response -> {
                    assertThat(response.accessToken()).isEqualTo("jwt.firmado");
                    assertThat(response.tokenType()).isEqualTo("Bearer");
                    assertThat(response.expiresIn()).isEqualTo(900);
                    assertThat(response.user().username()).isEqualTo("juanperez");
                    assertThat(response.user().email()).isEqualTo("juan@correo.com");
                })
                .verifyComplete();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userPersistence).save(captor.capture());
        User saved = captor.getValue();

        assertThat(saved.getPasswordHash()).isEqualTo("hash-bcrypt");
        assertThat(saved.getPasswordHash()).isNotEqualTo(PASSWORD);
        assertThat(saved.getRole()).isEqualTo(UserRole.USER);
        assertThat(saved.getAuthId()).isNotBlank();
        assertThat(UUID.fromString(saved.getAuthId())).isNotNull();
    }

    @Test
    void emailYUsernameSeConsultanYaNormalizados() {
        when(userPersistence.findByEmail(anyString())).thenReturn(Mono.empty());
        when(userPersistence.findByUsername(anyString())).thenReturn(Mono.empty());
        when(passwordEncoder.hash(PASSWORD)).thenReturn("hash-bcrypt");
        when(userPersistence.save(any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        service.registerWithoutToken(new RegisterRequest("JuanPerez", "Juan@Correo.com", PASSWORD))
                .block();

        verify(userPersistence).findByEmail("juan@correo.com");
        verify(userPersistence).findByUsername("juanperez");
    }

    @Test
    void emailDuplicadoDevuelveConflicto() {
        when(userPersistence.findByEmail("juan@correo.com"))
                .thenReturn(Mono.just(User.builder().id(1L).authId("a").username("juanperez")
                        .email("juan@correo.com").build()));

        StepVerifier.create(service.registerWithoutToken(
                        new RegisterRequest("juanperez", "juan@correo.com", PASSWORD)))
                .expectError(ConflictException.class)
                .verify();

        verify(userPersistence, never()).save(any());
        verify(passwordEncoder, never()).hash(any());
    }

    @Test
    void usernameDuplicadoDevuelveConflicto() {
        when(userPersistence.findByEmail(anyString())).thenReturn(Mono.empty());
        when(userPersistence.findByUsername("juanperez"))
                .thenReturn(Mono.just(User.builder().id(1L).authId("a").username("juanperez")
                        .email("otro@correo.com").build()));

        StepVerifier.create(service.registerWithoutToken(
                        new RegisterRequest("juanperez", "nuevo@correo.com", PASSWORD)))
                .expectError(ConflictException.class)
                .verify();

        verify(userPersistence, never()).save(any());
        verify(passwordEncoder, never()).hash(any());
    }

    @Test
    void emailInvalidoFallaAntesDeConsultarLaBase() {
        StepVerifier.create(service.registerWithoutToken(
                        new RegisterRequest("juanperez", "no-es-un-email", PASSWORD)))
                .expectError(DomainException.class)
                .verify();

        verify(userPersistence, never()).findByEmail(any());
        verify(userPersistence, never()).save(any());
    }

    @Test
    void passwordCortaFallaAntesDeConsultarLaBase() {
        StepVerifier.create(service.registerWithoutToken(
                        new RegisterRequest("juanperez", "juan@correo.com", "corta")))
                .expectError(DomainException.class)
                .verify();

        verify(userPersistence, never()).save(any());
    }

    @Test
    void usernameConCaracteresInvalidosFalla() {
        StepVerifier.create(service.registerWithoutToken(
                        new RegisterRequest("Juan Perez!", "juan@correo.com", PASSWORD)))
                .expectError(DomainException.class)
                .verify();

        verify(userPersistence, never()).save(any());
    }
}
