package com.bpao.devfoliobuilderapi.application.port.in.project;

import com.bpao.devfoliobuilderapi.application.dto.AuthenticatedUser;
import com.bpao.devfoliobuilderapi.application.dto.common.PageResponse;
import com.bpao.devfoliobuilderapi.application.dto.project.ProjectRequest;
import com.bpao.devfoliobuilderapi.application.dto.project.ProjectResponse;
import reactor.core.publisher.Mono;

/**
 * Puerto de entrada: CRUD de proyectos del portfolio del usuario autenticado.
 */
public interface ManageProjectUseCase {

    Mono<ProjectResponse> create(AuthenticatedUser authenticatedUser, ProjectRequest request);

    Mono<ProjectResponse> update(AuthenticatedUser authenticatedUser, Long projectId, ProjectRequest request);

    Mono<Void> delete(AuthenticatedUser authenticatedUser, Long projectId);

    Mono<PageResponse<ProjectResponse>> list(AuthenticatedUser authenticatedUser, int page, int size);
}