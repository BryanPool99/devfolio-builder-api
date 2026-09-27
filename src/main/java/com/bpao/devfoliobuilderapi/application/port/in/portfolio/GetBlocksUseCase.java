package com.bpao.devfoliobuilderapi.application.port.in.portfolio;

import com.bpao.devfoliobuilderapi.application.dto.AuthenticatedUser;
import com.bpao.devfoliobuilderapi.application.dto.portfolio.BlockResponse;
import reactor.core.publisher.Flux;

/**
 * Puerto de entrada: leer los bloques del portfolio del usuario autenticado.
 */
public interface GetBlocksUseCase {

    Flux<BlockResponse> getBlocks(AuthenticatedUser authenticatedUser);
}
