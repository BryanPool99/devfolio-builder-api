package com.bpao.devfoliobuilderapi.application.usecase.user;

import com.bpao.devfoliobuilderapi.application.dto.AuthenticatedUser;
import com.bpao.devfoliobuilderapi.application.port.in.user.GetCurrentUserUseCase;
import com.bpao.devfoliobuilderapi.application.port.out.user.UserPersistencePort;
import com.bpao.devfoliobuilderapi.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Locale;

/**
 * Caso de uso: federacion del usuario con Neon Auth.
 * Si el usuario ya existe (por authId) se devuelve; si no, se crea.
 */
@Service
@RequiredArgsConstructor
public class GetCurrentUserService implements GetCurrentUserUseCase {

    private final UserPersistencePort userPersistence;

    @Override
    public Mono<User> findOrCreate(AuthenticatedUser authenticatedUser) {
        return userPersistence.findByAuthId(authenticatedUser.authId())
                .switchIfEmpty(Mono.defer(() -> createNewUser(authenticatedUser)));
    }

    private Mono<User> createNewUser(AuthenticatedUser authenticatedUser) {
        User user = User.create(
                authenticatedUser.authId(),
                resolveUsername(authenticatedUser),
                authenticatedUser.email());
        return userPersistence.save(user);
    }

    /**
     * Slug para /p/{username}: primero preferred_username; si falta, el prefijo
     * local del email. La unicidad definitiva se reforzara con un generador
     * dedicado ante conflictos (trabajo futuro).
     */
    private String resolveUsername(AuthenticatedUser authenticatedUser) {
        String candidate = authenticatedUser.preferredUsername();
        if (candidate == null || candidate.isBlank()) {
            candidate = authenticatedUser.email();
        }
        if (candidate == null || candidate.isBlank()) {
            throw new IllegalArgumentException("El token no trae username ni email");
        }
        int at = candidate.indexOf('@');
        String base = at > 0 ? candidate.substring(0, at) : candidate;
        return base.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", "-");
    }
}