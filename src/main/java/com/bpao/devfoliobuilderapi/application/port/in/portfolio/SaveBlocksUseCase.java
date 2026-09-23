package com.bpao.devfoliobuilderapi.application.port.in.portfolio;

import com.bpao.devfoliobuilderapi.application.dto.AuthenticatedUser;
import com.bpao.devfoliobuilderapi.application.dto.portfolio.BlockRequest;
import com.bpao.devfoliobuilderapi.application.dto.portfolio.BlockResponse;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Puerto de entrada: guardar/reordenar los bloques del portfolio.
 */
public interface SaveBlocksUseCase {

    Flux<BlockResponse> save(AuthenticatedUser authenticatedUser, List<BlockRequest> blocks);
}