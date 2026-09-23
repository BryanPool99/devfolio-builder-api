package com.bpao.devfoliobuilderapi.application.usecase.project;

import com.bpao.devfoliobuilderapi.application.dto.AuthenticatedUser;
import com.bpao.devfoliobuilderapi.application.dto.common.PageResponse;
import com.bpao.devfoliobuilderapi.application.dto.project.ProjectRequest;
import com.bpao.devfoliobuilderapi.application.dto.project.ProjectResponse;
import com.bpao.devfoliobuilderapi.application.dto.user.SyncResult;
import com.bpao.devfoliobuilderapi.application.port.in.user.SyncUserUseCase;
import com.bpao.devfoliobuilderapi.application.port.out.catalog.CatalogPersistencePort;
import com.bpao.devfoliobuilderapi.application.port.out.project.ProjectPersistencePort;
import com.bpao.devfoliobuilderapi.application.port.out.project.ProjectTechnologyPersistencePort;
import com.bpao.devfoliobuilderapi.domain.exception.DomainException;
import com.bpao.devfoliobuilderapi.domain.exception.NotFoundException;
import com.bpao.devfoliobuilderapi.domain.model.Portfolio;
import com.bpao.devfoliobuilderapi.domain.model.Project;
import com.bpao.devfoliobuilderapi.domain.model.Technology;
import com.bpao.devfoliobuilderapi.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManageProjectServiceTest {

    private static final long PORTFOLIO_ID = 10L;

    @Mock
    private SyncUserUseCase syncUserUseCase;
    @Mock
    private ProjectPersistencePort projectPersistence;
    @Mock
    private ProjectTechnologyPersistencePort projectTechnologyPersistence;
    @Mock
    private CatalogPersistencePort catalogPersistence;
    @Mock
    private ProjectResponseAssembler projectResponseAssembler;
    @Mock
    private TransactionalOperator transactionalOperator;

    @InjectMocks
    private ProjectManagementService service;

    private final AuthenticatedUser principal = new AuthenticatedUser("auth-1", "juan@correo.com", "JuanPerez");

    @Test
    void createGuardaProyectoYAsociaTecnologiasEnTransaccion() {
        withPortfolio();
        when(catalogPersistence.findByIds(List.of(1L, 2L)))
                .thenReturn(Flux.just(new Technology(1L, "Java", "/java.svg", 1L),
                        new Technology(2L, "Spring", "/spring.svg", 1L)));
        Project saved = Project.builder()
                .id(100L).portfolioId(PORTFOLIO_ID).title("Mi app").createdAt(java.time.Instant.now()).build();
        when(projectPersistence.save(any(Project.class))).thenReturn(Mono.just(saved));
        when(projectTechnologyPersistence.saveAll(100L, List.of(1L, 2L))).thenReturn(Mono.empty());
        when(projectResponseAssembler.toResponse(saved))
                .thenReturn(Mono.just(new ProjectResponse(100L, "Mi app", null, null, null, null, null, List.of())));
        passthroughTransaction();

        StepVerifier.create(service.create(principal, new ProjectRequest(
                        "Mi app", "desc", "https://repo", "https://demo", null, List.of(1L, 2L))))
                .assertNext(response -> assertThat(response.title()).isEqualTo("Mi app"))
                .verifyComplete();

        verify(projectPersistence).save(argThat(project -> project.getTitle().equals("Mi app")));
        verify(projectTechnologyPersistence).saveAll(100L, List.of(1L, 2L));
    }

    @Test
    void createSinTecnologiasNoConsultaCatalogo() {
        withPortfolio();
        Project saved = Project.builder()
                .id(100L).portfolioId(PORTFOLIO_ID).title("Mi app").createdAt(java.time.Instant.now()).build();
        when(projectPersistence.save(any(Project.class))).thenReturn(Mono.just(saved));
        when(projectTechnologyPersistence.saveAll(100L, List.of())).thenReturn(Mono.empty());
        when(projectResponseAssembler.toResponse(saved))
                .thenReturn(Mono.just(new ProjectResponse(100L, "Mi app", null, null, null, null, null, List.of())));
        passthroughTransaction();

        StepVerifier.create(service.create(principal, new ProjectRequest(
                        "Mi app", null, null, null, null, null)))
                .assertNext(response -> assertThat(response.title()).isEqualTo("Mi app"))
                .verifyComplete();

        verify(projectTechnologyPersistence).saveAll(100L, List.of());
        verify(catalogPersistence, never()).findByIds(any());
    }

    @Test
    void createRechazaTecnologiaInexistente() {
        withPortfolio();
        when(catalogPersistence.findByIds(List.of(1L, 99L)))
                .thenReturn(Flux.just(new Technology(1L, "Java", "/java.svg", 1L)));

        StepVerifier.create(service.create(principal, new ProjectRequest(
                        "Mi app", null, null, null, null, List.of(1L, 99L))))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DomainException.class);
                    assertThat(error.getMessage()).contains("99");
                })
                .verify();

        verify(projectPersistence, never()).save(any());
    }

    @Test
    void updateReemplazaTecnologiasDelProyecto() {
        withPortfolio();
        Project existing = Project.builder()
                .id(200L).portfolioId(PORTFOLIO_ID).title("Antes").createdAt(java.time.Instant.now()).build();
        when(projectPersistence.findById(200L)).thenReturn(Mono.just(existing));
        when(catalogPersistence.findByIds(List.of(3L)))
                .thenReturn(Flux.just(new Technology(3L, "React", "/react.svg", 9L)));
        when(projectTechnologyPersistence.deleteByProjectId(200L)).thenReturn(Mono.empty());
        when(projectPersistence.save(any(Project.class))).thenReturn(Mono.just(existing));
        when(projectTechnologyPersistence.saveAll(200L, List.of(3L))).thenReturn(Mono.empty());
        when(projectResponseAssembler.toResponse(any(Project.class)))
                .thenReturn(Mono.just(new ProjectResponse(200L, "Despues", null, null, null, null, null, List.of())));
        passthroughTransaction();

        StepVerifier.create(service.update(principal, 200L, new ProjectRequest(
                        "Despues", null, null, null, null, List.of(3L))))
                .assertNext(response -> assertThat(response.title()).isEqualTo("Despues"))
                .verifyComplete();

        verify(projectTechnologyPersistence).deleteByProjectId(200L);
        verify(projectTechnologyPersistence).saveAll(200L, List.of(3L));
    }

    @Test
    void updateConProyectoDeOtroPortfolioDevuelve404() {
        withPortfolio();
        Project foreign = Project.builder()
                .id(300L).portfolioId(99L).title("Ajeno").createdAt(java.time.Instant.now()).build();
        when(projectPersistence.findById(300L)).thenReturn(Mono.just(foreign));

        StepVerifier.create(service.update(principal, 300L, new ProjectRequest(
                        "X", null, null, null, null, List.of())))
                .expectError(NotFoundException.class)
                .verify();

        verify(projectPersistence, never()).save(any());
    }

    @Test
    void deleteBorraAsociacionesYProyecto() {
        withPortfolio();
        Project existing = Project.builder()
                .id(200L).portfolioId(PORTFOLIO_ID).title("Mi app").createdAt(java.time.Instant.now()).build();
        when(projectPersistence.findById(200L)).thenReturn(Mono.just(existing));
        when(projectTechnologyPersistence.deleteByProjectId(200L)).thenReturn(Mono.empty());
        when(projectPersistence.deleteById(200L)).thenReturn(Mono.empty());
        passthroughTransaction();

        StepVerifier.create(service.delete(principal, 200L)).verifyComplete();

        verify(projectTechnologyPersistence).deleteByProjectId(200L);
        verify(projectPersistence).deleteById(200L);
    }

    @Test
    void listDelegaLaPaginacionAlEnsamblador() {
        withPortfolio();
        PageResponse<ProjectResponse> page = PageResponse.of(List.of(
                new ProjectResponse(500L, "Uno", null, null, null, null, null, List.of())), 0, 2, 1L);
        when(projectResponseAssembler.pageByPortfolio(PORTFOLIO_ID, 0, 2)).thenReturn(Mono.just(page));

        StepVerifier.create(service.list(principal, 0, 2))
                .assertNext(response -> {
                    assertThat(response.page()).isZero();
                    assertThat(response.size()).isEqualTo(2);
                    assertThat(response.totalElements()).isEqualTo(1L);
                    assertThat(response.totalPages()).isEqualTo(1);
                    assertThat(response.content()).extracting(ProjectResponse::title)
                            .containsExactly("Uno");
                })
                .verifyComplete();
    }

    private void withPortfolio() {
        User user = User.builder().id(1L).authId("auth-1").username("juanperez").email("juan@correo.com").build();
        Portfolio portfolio = Portfolio.builder().id(PORTFOLIO_ID).userId(1L).title("Mi Portafolio").build();
        when(syncUserUseCase.sync(principal)).thenReturn(Mono.just(new SyncResult(user, portfolio)));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void passthroughTransaction() {
        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }
}