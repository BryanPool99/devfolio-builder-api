package com.bpao.devfoliobuilderapi.application.usecase.portfolio;

import com.bpao.devfoliobuilderapi.application.dto.AuthenticatedUser;
import com.bpao.devfoliobuilderapi.application.dto.portfolio.BlockMapper;
import com.bpao.devfoliobuilderapi.application.dto.portfolio.BlockResponse;
import com.bpao.devfoliobuilderapi.application.port.in.portfolio.GetBlocksUseCase;
import com.bpao.devfoliobuilderapi.application.port.in.user.SyncUserUseCase;
import com.bpao.devfoliobuilderapi.application.port.out.portfolio.PortfolioBlockPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Caso de uso: leer los bloques del portfolio del usuario autenticado,
 * ordenados por posicion (garantizado por la consulta de persistencia).
 */
@Service
@RequiredArgsConstructor
public class GetBlocksService implements GetBlocksUseCase {

    private final SyncUserUseCase syncUserUseCase;
    private final PortfolioBlockPersistencePort portfolioBlockPersistence;

    @Override
    public Flux<BlockResponse> getBlocks(AuthenticatedUser authenticatedUser) {
        return currentPortfolioId(authenticatedUser)
                .flatMapMany(portfolioBlockPersistence::findByPortfolioId)
                .map(BlockMapper::toResponse);
    }

    private Mono<Long> currentPortfolioId(AuthenticatedUser authenticatedUser) {
        return syncUserUseCase.sync(authenticatedUser)
                .map(result -> result.portfolio().getId());
    }
}
