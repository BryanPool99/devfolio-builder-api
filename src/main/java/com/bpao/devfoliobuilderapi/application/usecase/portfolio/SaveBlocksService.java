package com.bpao.devfoliobuilderapi.application.usecase.portfolio;

import com.bpao.devfoliobuilderapi.application.dto.AuthenticatedUser;
import com.bpao.devfoliobuilderapi.application.dto.portfolio.BlockMapper;
import com.bpao.devfoliobuilderapi.application.dto.portfolio.BlockRequest;
import com.bpao.devfoliobuilderapi.application.dto.portfolio.BlockResponse;
import com.bpao.devfoliobuilderapi.application.port.in.portfolio.SaveBlocksUseCase;
import com.bpao.devfoliobuilderapi.application.port.in.user.SyncUserUseCase;
import com.bpao.devfoliobuilderapi.application.port.out.portfolio.PortfolioBlockPersistencePort;
import com.bpao.devfoliobuilderapi.domain.exception.DomainException;
import com.bpao.devfoliobuilderapi.domain.model.PortfolioBlock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * Caso de uso: guardar y reordenar bloques del portfolio (semantica replace-all).
 * La lista recibida representa el estado completo: se eliminan los bloques
 * existentes y se insertan los nuevos con position 0..n, todo en transaccion.
 */
@Service
@RequiredArgsConstructor
public class SaveBlocksService implements SaveBlocksUseCase {

    private final SyncUserUseCase syncUserUseCase;
    private final PortfolioBlockPersistencePort portfolioBlockPersistence;
    private final TransactionalOperator transactionalOperator;

    @Override
    public Flux<BlockResponse> save(AuthenticatedUser authenticatedUser, List<BlockRequest> blocks) {
        return currentPortfolioId(authenticatedUser)
                .flatMapMany(portfolioId -> transactionalOperator.transactional(
                        portfolioBlockPersistence.deleteAllByPortfolioId(portfolioId)
                                .thenMany(saveAll(portfolioId, blocks))))
                .map(BlockMapper::toResponse);
    }

    private Flux<PortfolioBlock> saveAll(Long portfolioId, List<BlockRequest> requests) {
        List<PortfolioBlock> blocks = new ArrayList<>(requests.size());
        for (int i = 0; i < requests.size(); i++) {
            blocks.add(PortfolioBlock.create(portfolioId, requiredType(requests.get(i)), i, requests.get(i).settings()));
        }
        return portfolioBlockPersistence.saveAll(blocks);
    }

    private Mono<Long> currentPortfolioId(AuthenticatedUser authenticatedUser) {
        return syncUserUseCase.sync(authenticatedUser)
                .map(result -> result.portfolio().getId());
    }

    private String requiredType(BlockRequest request) {
        if (request.type() == null || request.type().isBlank()) {
            throw new DomainException("El tipo de bloque no puede ser nulo ni vacio");
        }
        return request.type().trim();
    }
}