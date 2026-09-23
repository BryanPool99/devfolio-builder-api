package com.bpao.devfoliobuilderapi.application.port.in.portfolio;

import com.bpao.devfoliobuilderapi.application.dto.portfolio.PublicPortfolioResponse;
import reactor.core.publisher.Mono;

/**
 * Puerto de entrada: lectura publica de un portafolio por username.
 * Los proyectos se devuelven paginados (offset/limit).
 */
public interface GetPublicPortfolioUseCase {

    Mono<PublicPortfolioResponse> getByUsername(String username, int page, int size);
}