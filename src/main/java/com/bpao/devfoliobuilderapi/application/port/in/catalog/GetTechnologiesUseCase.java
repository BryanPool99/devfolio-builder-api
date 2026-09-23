package com.bpao.devfoliobuilderapi.application.port.in.catalog;

import com.bpao.devfoliobuilderapi.application.dto.catalog.TechnologyResponse;
import reactor.core.publisher.Flux;

/**
 * Puerto de entrada: consulta del catalogo de tecnologias.
 */
public interface GetTechnologiesUseCase {

    Flux<TechnologyResponse> getTechnologies(Long categoryId);
}