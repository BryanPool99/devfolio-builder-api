package com.bpao.devfoliobuilderapi.infrastructure.adapter.in.web;

import com.bpao.devfoliobuilderapi.application.dto.AuthenticatedUser;
import com.bpao.devfoliobuilderapi.application.dto.common.PageResponse;
import com.bpao.devfoliobuilderapi.application.dto.project.ProjectRequest;
import com.bpao.devfoliobuilderapi.application.dto.project.ProjectResponse;
import com.bpao.devfoliobuilderapi.application.port.in.project.ManageProjectUseCase;
import com.bpao.devfoliobuilderapi.infrastructure.security.AuthenticatedUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * CRUD de proyectos del portfolio del usuario autenticado.
 */
@RestController
@RequestMapping("/api/v1/portfolio/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ManageProjectUseCase manageProjectUseCase;

    @GetMapping
    public Mono<PageResponse<ProjectResponse>> list(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return manageProjectUseCase.list(AuthenticatedUserMapper.from(jwt), page, size);
    }

    @PostMapping
    public Mono<ResponseEntity<ProjectResponse>> create(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody ProjectRequest request) {
        return manageProjectUseCase.create(AuthenticatedUserMapper.from(jwt), request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @PutMapping("/{id}")
    public Mono<ProjectResponse> update(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @RequestBody ProjectRequest request) {
        return manageProjectUseCase.update(AuthenticatedUserMapper.from(jwt), id, request);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return manageProjectUseCase.delete(AuthenticatedUserMapper.from(jwt), id)
                .thenReturn(ResponseEntity.noContent().build());
    }
}