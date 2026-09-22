package com.bpao.devfoliobuilderapi.application.usecase.user;

import com.bpao.devfoliobuilderapi.application.dto.AuthenticatedUser;
import com.bpao.devfoliobuilderapi.application.port.out.user.UserPersistencePort;
import com.bpao.devfoliobuilderapi.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCurrentUserServiceTest {

    @Mock
    private UserPersistencePort userPersistence;

    @InjectMocks
    private GetCurrentUserService service;

    private final AuthenticatedUser principal =
            new AuthenticatedUser("auth-123", "juan@correo.com", "JuanPerez");

    @Test
    void devuelveElUsuarioExistenteSinPersistir() {
        User existing = User.builder()
                .id(1L)
                .authId("auth-123")
                .username("JuanPerez")
                .email("juan@correo.com")
                .createdAt(Instant.now())
                .build();
        when(userPersistence.findByAuthId("auth-123")).thenReturn(Mono.just(existing));

        StepVerifier.create(service.findOrCreate(principal))
                .expectNextMatches(user -> user.getId().equals(1L))
                .verifyComplete();

        verify(userPersistence, never()).save(any());
    }

    @Test
    void creaElUsuarioCuandoNoExiste() {
        when(userPersistence.findByAuthId("auth-123")).thenReturn(Mono.empty());
        when(userPersistence.save(any(User.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0, User.class)));

        StepVerifier.create(service.findOrCreate(principal))
                .assertNext(user -> {
                    assertThat(user.getAuthId()).isEqualTo("auth-123");
                    assertThat(user.getUsername()).isEqualTo("juanperez");
                    assertThat(user.getEmail()).isEqualTo("juan@correo.com");
                })
                .verifyComplete();

        verify(userPersistence).save(User.create("auth-123", "juanperez", "juan@correo.com"));
    }
}