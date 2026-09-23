package com.bpao.devfoliobuilderapi.application.usecase.project;

import com.bpao.devfoliobuilderapi.application.dto.AuthenticatedUser;
import com.bpao.devfoliobuilderapi.application.dto.common.PageResponse;
import com.bpao.devfoliobuilderapi.application.dto.project.ProjectRequest;
import com.bpao.devfoliobuilderapi.application.dto.project.ProjectResponse;
import com.bpao.devfoliobuilderapi.application.port.in.project.ManageProjectUseCase;
import com.bpao.devfoliobuilderapi.application.port.in.user.SyncUserUseCase;
import com.bpao.devfoliobuilderapi.application.port.out.catalog.CatalogPersistencePort;
import com.bpao.devfoliobuilderapi.application.port.out.project.ProjectPersistencePort;
import com.bpao.devfoliobuilderapi.application.port.out.project.ProjectTechnologyPersistencePort;
import com.bpao.devfoliobuilderapi.domain.exception.DomainException;
import com.bpao.devfoliobuilderapi.domain.exception.NotFoundException;
import com.bpao.devfoliobuilderapi.domain.model.Project;
import com.bpao.devfoliobuilderapi.domain.model.Technology;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Caso de uso: CRUD de proyectos del portfolio del usuario autenticado.
 * Los proyectos se asocian a tecnologias del catalogo global y siempre se
 * acotan al portfolio del usuario (resuelto via auto-sync).
 */
@Service
@RequiredArgsConstructor
public class ProjectManagementService implements ManageProjectUseCase {

    private final SyncUserUseCase syncUserUseCase;
    private final ProjectPersistencePort projectPersistence;
    private final ProjectTechnologyPersistencePort projectTechnologyPersistence;
    private final CatalogPersistencePort catalogPersistence;
    private final ProjectResponseAssembler projectResponseAssembler;
    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<ProjectResponse> create(AuthenticatedUser authenticatedUser, ProjectRequest request) {
        return currentPortfolioId(authenticatedUser)
                .flatMap(portfolioId -> requireTechnologiesExist(request)
                        .then(Mono.defer(() -> createProject(portfolioId, request))));
    }

    @Override
    public Mono<ProjectResponse> update(AuthenticatedUser authenticatedUser, Long projectId, ProjectRequest request) {
        return currentPortfolioId(authenticatedUser)
                .flatMap(portfolioId -> requireOwnedProject(projectId, portfolioId))
                .flatMap(existing -> requireTechnologiesExist(request)
                        .then(Mono.defer(() -> updateProject(existing, request))));
    }

    @Override
    public Mono<Void> delete(AuthenticatedUser authenticatedUser, Long projectId) {
        return currentPortfolioId(authenticatedUser)
                .flatMap(portfolioId -> requireOwnedProject(projectId, portfolioId))
                .flatMap(project -> transactionalOperator.transactional(
                        projectTechnologyPersistence.deleteByProjectId(project.getId())
                                .then(projectPersistence.deleteById(project.getId()))));
    }

    @Override
    public Mono<PageResponse<ProjectResponse>> list(AuthenticatedUser authenticatedUser, int page, int size) {
        return currentPortfolioId(authenticatedUser)
                .flatMap(portfolioId -> projectResponseAssembler.pageByPortfolio(portfolioId, page, size));
    }

    private Mono<Long> currentPortfolioId(AuthenticatedUser authenticatedUser) {
        return syncUserUseCase.sync(authenticatedUser)
                .map(result -> result.portfolio().getId());
    }

    private Mono<Project> requireOwnedProject(Long projectId, Long portfolioId) {
        return projectPersistence.findById(projectId)
                .switchIfEmpty(Mono.error(new NotFoundException("Proyecto no encontrado: " + projectId)))
                .flatMap(project -> project.getPortfolioId().equals(portfolioId)
                        ? Mono.just(project)
                        : Mono.error(new NotFoundException("Proyecto no encontrado: " + projectId)));
    }

    private Mono<Void> requireTechnologiesExist(ProjectRequest request) {
        Collection<Long> ids = technologyIds(request);
        if (ids.isEmpty()) {
            return Mono.empty();
        }
        return catalogPersistence.findByIds(ids)
                .map(Technology::getId)
                .collect(Collectors.toSet())
                .flatMap(found -> {
                    Set<Long> missing = ids.stream().filter(id -> !found.contains(id)).collect(Collectors.toSet());
                    if (!missing.isEmpty()) {
                        return Mono.error(new DomainException("Tecnologias no encontradas: " + missing));
                    }
                    return Mono.empty();
                });
    }

    private Mono<ProjectResponse> createProject(Long portfolioId, ProjectRequest request) {
        Project project = Project.create(portfolioId, request.title(), request.description(),
                request.repositoryUrl(), request.liveDemoUrl(), request.imageUrl());
        Mono<ProjectResponse> creation = projectPersistence.save(project)
                .flatMap(saved -> projectTechnologyPersistence
                        .saveAll(saved.getId(), technologyIds(request))
                        .thenReturn(saved))
                .flatMap(projectResponseAssembler::toResponse);
        return transactionalOperator.transactional(creation);
    }

    private Mono<ProjectResponse> updateProject(Project existing, ProjectRequest request) {
        Project updated = existing.withDetails(request.title(), request.description(),
                request.repositoryUrl(), request.liveDemoUrl(), request.imageUrl());
        Mono<ProjectResponse> update = projectTechnologyPersistence.deleteByProjectId(updated.getId())
                .then(projectPersistence.save(updated))
                .flatMap(saved -> projectTechnologyPersistence
                        .saveAll(saved.getId(), technologyIds(request))
                        .thenReturn(saved))
                .flatMap(projectResponseAssembler::toResponse);
        return transactionalOperator.transactional(update);
    }

    private List<Long> technologyIds(ProjectRequest request) {
        return request.technologyIds() == null ? List.of() : request.technologyIds();
    }
}