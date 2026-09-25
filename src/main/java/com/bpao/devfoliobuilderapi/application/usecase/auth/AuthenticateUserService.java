package com.bpao.devfoliobuilderapi.application.usecase.auth;

import com.bpao.devfoliobuilderapi.application.dto.auth.AuthRequest;
import com.bpao.devfoliobuilderapi.application.dto.auth.TokenResponse;
import com.bpao.devfoliobuilderapi.application.dto.user.UserMapper;
import com.bpao.devfoliobuilderapi.application.port.in.auth.AuthenticateUserUseCase;
import com.bpao.devfoliobuilderapi.application.port.out.security.PasswordEncoderPort;
import com.bpao.devfoliobuilderapi.application.port.out.security.TokenIssuerPort;
import com.bpao.devfoliobuilderapi.application.port.out.user.UserPersistencePort;
import com.bpao.devfoliobuilderapi.domain.exception.DomainException;
import com.bpao.devfoliobuilderapi.domain.exception.InvalidCredentialsException;
import com.bpao.devfoliobuilderapi.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Locale;

/**
 * Caso de uso: autenticacion por credenciales propias.
 * El identificador admite email o username. Ante cualquier fallo se devuelve el
 * mismo error generico para no revelar si el usuario existe.
 */
@Service
@RequiredArgsConstructor
public class AuthenticateUserService implements AuthenticateUserUseCase {

    private static final String INVALID_CREDENTIALS = "Credenciales invalidas";

    private final UserPersistencePort userPersistence;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenIssuerPort tokenIssuer;

    @Override
    public Mono<TokenResponse> authenticate(AuthRequest request) {
        if (request == null) {
            return Mono.error(new DomainException("El cuerpo de la peticion es obligatorio"));
        }
        return Mono.fromCallable(() -> identifier(request))
                .flatMap(this::findUser)
                // Sin switchIfEmpty un usuario inexistente devolveria un Mono vacio
                // y el controller responderia 200 sin cuerpo.
                .switchIfEmpty(Mono.error(new InvalidCredentialsException(INVALID_CREDENTIALS)))
                .flatMap(user -> verifyPassword(user, request.password()))
                .map(this::toResponse);
    }

    private String identifier(AuthRequest request) {
        String identifier = request.identifier();
        if (identifier == null || identifier.isBlank()) {
            throw new DomainException("El identificador es obligatorio");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new DomainException("La contrasena es obligatoria");
        }
        return identifier.trim().toLowerCase(Locale.ROOT);
    }

    private Mono<User> findUser(String identifier) {
        return userPersistence.findByEmail(identifier)
                .switchIfEmpty(Mono.defer(() -> userPersistence.findByUsername(identifier)));
    }

    private Mono<User> verifyPassword(User user, String rawPassword) {
        String hash = user.getPasswordHash();
        if (hash == null || hash.isBlank() || !passwordEncoder.matches(rawPassword, hash)) {
            return Mono.error(new InvalidCredentialsException(INVALID_CREDENTIALS));
        }
        return Mono.just(user);
    }

    private TokenResponse toResponse(User user) {
        TokenIssuerPort.IssuedToken token = tokenIssuer.issue(user);
        return new TokenResponse(
                token.value(),
                "Bearer",
                token.expiresInSeconds(),
                UserMapper.toResponse(user));
    }
}
