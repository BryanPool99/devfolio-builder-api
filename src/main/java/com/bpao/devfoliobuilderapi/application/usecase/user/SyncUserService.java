package com.bpao.devfoliobuilderapi.application.usecase.user;

import com.bpao.devfoliobuilderapi.application.dto.AuthenticatedUser;
import com.bpao.devfoliobuilderapi.application.dto.user.SyncResult;
import com.bpao.devfoliobuilderapi.application.port.in.user.SyncUserUseCase;
import com.bpao.devfoliobuilderapi.application.port.out.portfolio.PortfolioBlockPersistencePort;
import com.bpao.devfoliobuilderapi.application.port.out.portfolio.PortfolioPersistencePort;
import com.bpao.devfoliobuilderapi.application.port.out.user.UserPersistencePort;
import com.bpao.devfoliobuilderapi.domain.model.Portfolio;
import com.bpao.devfoliobuilderapi.domain.model.PortfolioBlock;
import com.bpao.devfoliobuilderapi.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.util.Locale;

/**
 * Caso de uso: sincronizacion del usuario autenticado.
 * Con el registro propio el usuario ya existe, asi que en la practica solo
 * asegura el portfolio por defecto y el bloque HERO inicial. Los registros
 * posteriores solo devuelven lo existente.
 */
@Service
@RequiredArgsConstructor
public class SyncUserService implements SyncUserUseCase {

    private final UserPersistencePort userPersistence;
    private final PortfolioPersistencePort portfolioPersistence;
    private final PortfolioBlockPersistencePort portfolioBlockPersistence;
    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<SyncResult> sync(AuthenticatedUser authenticatedUser) {
        return userPersistence.findByAuthId(authenticatedUser.authId())
                .switchIfEmpty(Mono.defer(() -> createNewUser(authenticatedUser)))
                .flatMap(this::ensurePortfolio);
    }

    private Mono<User> createNewUser(AuthenticatedUser authenticatedUser) {
        User user = User.create(
                authenticatedUser.authId(),
                resolveUsername(authenticatedUser),
                authenticatedUser.email());
        return userPersistence.save(user);
    }

    private Mono<SyncResult> ensurePortfolio(User user) {
        return portfolioPersistence.findByUserId(user.getId())
                .switchIfEmpty(Mono.defer(() -> createDefaultPortfolio(user)))
                .map(portfolio -> new SyncResult(user, portfolio));
    }

    private Mono<Portfolio> createDefaultPortfolio(User user) {
        Mono<Portfolio> creation = portfolioPersistence.save(Portfolio.createDefault(user.getId()))
                .flatMap(portfolio -> portfolioBlockPersistence
                        .save(PortfolioBlock.createHero(portfolio.getId()))
                        .thenReturn(portfolio));
        return transactionalOperator.transactional(creation);
    }

    /**
     * Slug para la vista publica /api/v1/public/portfolios/{username}: primero
     * preferred_username; si falta, el prefijo local del email. La unicidad
     * definitiva se reforzara con un generador dedicado ante conflictos
     * (trabajo futuro).
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