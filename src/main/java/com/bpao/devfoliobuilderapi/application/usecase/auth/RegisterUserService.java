package com.bpao.devfoliobuilderapi.application.usecase.auth;

import com.bpao.devfoliobuilderapi.application.dto.auth.RegisterRequest;
import com.bpao.devfoliobuilderapi.application.dto.auth.TokenResponse;
import com.bpao.devfoliobuilderapi.application.dto.user.UserMapper;
import com.bpao.devfoliobuilderapi.application.port.in.auth.RegisterUserUseCase;
import com.bpao.devfoliobuilderapi.application.port.out.security.PasswordEncoderPort;
import com.bpao.devfoliobuilderapi.application.port.out.security.TokenIssuerPort;
import com.bpao.devfoliobuilderapi.application.port.out.user.UserPersistencePort;
import com.bpao.devfoliobuilderapi.domain.exception.ConflictException;
import com.bpao.devfoliobuilderapi.domain.exception.DomainException;
import com.bpao.devfoliobuilderapi.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Caso de uso: alta de usuario con credenciales propias.
 * El portfolio por defecto no se crea aqui; sigue siendo asegurado por
 * SyncUserService en la primera llamada a /users/me.
 */
@Service
@RequiredArgsConstructor
public class RegisterUserService implements RegisterUserUseCase {

    private final UserPersistencePort userPersistence;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenIssuerPort tokenIssuer;

    @Override
    public Mono<TokenResponse> register(RegisterRequest request) {
        return registerWithoutToken(request)
                .map(user -> {
                    TokenIssuerPort.IssuedToken token = tokenIssuer.issue(user);
                    return new TokenResponse(
                            token.value(),
                            "Bearer",
                            token.expiresInSeconds(),
                            UserMapper.toResponse(user));
                });
    }

    @Override
    public Mono<User> registerWithoutToken(RegisterRequest request) {
        if (request == null) {
            return Mono.error(new DomainException("El cuerpo de la peticion es obligatorio"));
        }
        return Mono.defer(() -> persist(request));
    }

    private Mono<User> persist(RegisterRequest request) {
        User.validateCredentials(request.username(), request.email(), request.password());
        String username = User.normalizeUsername(request.username());
        String email = User.normalizeEmail(request.email());
        String authId = UUID.randomUUID().toString();
        return ensureAvailable(username, email)
                // El hash se calcula solo cuando ya sabemos que no hay conflicto.
                .then(Mono.fromCallable(() -> passwordEncoder.hash(request.password())))
                .flatMap(passwordHash -> userPersistence.save(
                        User.register(authId, username, email, request.password(), passwordHash)));
    }

    private Mono<Void> ensureAvailable(String username, String email) {
        return userPersistence.findByEmail(email)
                .flatMap(existing -> Mono.<Void>error(new ConflictException("El email ya esta registrado")))
                .switchIfEmpty(Mono.defer(() -> userPersistence.findByUsername(username)
                        .flatMap(existing -> Mono.<Void>error(
                                new ConflictException("El username ya esta en uso")))))
                .then();
    }
}
