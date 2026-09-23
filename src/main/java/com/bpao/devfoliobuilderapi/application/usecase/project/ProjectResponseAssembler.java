package com.bpao.devfoliobuilderapi.application.usecase.project;

import com.bpao.devfoliobuilderapi.application.dto.common.PageResponse;
import com.bpao.devfoliobuilderapi.application.dto.project.ProjectMapper;
import com.bpao.devfoliobuilderapi.application.dto.project.ProjectResponse;
import com.bpao.devfoliobuilderapi.application.port.out.catalog.CatalogPersistencePort;
import com.bpao.devfoliobuilderapi.application.port.out.project.ProjectPersistencePort;
import com.bpao.devfoliobuilderapi.application.port.out.project.ProjectTechnologyPersistencePort;
import com.bpao.devfoliobuilderapi.domain.model.Project;
import com.bpao.devfoliobuilderapi.domain.model.Technology;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Ensambla ProjectResponse (proyecto + tecnologias) para reutilizarlo en
 * el CRUD del editor y en la vista publica del portafolio. Tambien arma la
 * pagina de proyectos (offset/limit) compartida por ambos.
 */
@Component
@RequiredArgsConstructor
public class ProjectResponseAssembler {

    public static final int MAX_SIZE = 100;

    private final ProjectPersistencePort projectPersistence;
    private final ProjectTechnologyPersistencePort projectTechnologyPersistence;
    private final CatalogPersistencePort catalogPersistence;

    public Mono<ProjectResponse> toResponse(Project project) {
        return projectTechnologyPersistence.findTechnologyIdsByProjectId(project.getId())
                .collectList()
                .flatMap(this::resolveTechnologies)
                .map(technologies -> ProjectMapper.toResponse(project, technologies));
    }

    public Mono<PageResponse<ProjectResponse>> pageByPortfolio(Long portfolioId, int page, int size) {
        requireValidPage(page, size);
        Mono<List<ProjectResponse>> content = projectPersistence
                .findPageByPortfolioId(portfolioId, size, (long) page * size)
                .concatMap(this::toResponse)
                .collectList();
        Mono<Long> total = projectPersistence.countByPortfolioId(portfolioId);
        return Mono.zip(content, total,
                (items, count) -> PageResponse.of(items, page, size, count));
    }

    private void requireValidPage(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("page no puede ser negativo");
        }
        if (size < 1 || size > MAX_SIZE) {
            throw new IllegalArgumentException("size debe estar entre 1 y " + MAX_SIZE);
        }
    }

    private Mono<List<Technology>> resolveTechnologies(List<Long> technologyIds) {
        if (technologyIds.isEmpty()) {
            return Mono.just(List.of());
        }
        return catalogPersistence.findByIds(technologyIds).collectList();
    }
}