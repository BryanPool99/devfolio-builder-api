package com.bpao.devfoliobuilderapi.application.usecase.project;

import com.bpao.devfoliobuilderapi.application.dto.common.PageResponse;
import com.bpao.devfoliobuilderapi.application.port.out.catalog.CatalogPersistencePort;
import com.bpao.devfoliobuilderapi.application.port.out.project.ProjectPersistencePort;
import com.bpao.devfoliobuilderapi.application.port.out.project.ProjectTechnologyPersistencePort;
import com.bpao.devfoliobuilderapi.domain.model.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectResponseAssemblerTest {

    private static final long PORTFOLIO_ID = 10L;

    @Mock
    private ProjectPersistencePort projectPersistence;
    @Mock
    private ProjectTechnologyPersistencePort projectTechnologyPersistence;
    @Mock
    private CatalogPersistencePort catalogPersistence;

    @InjectMocks
    private ProjectResponseAssembler assembler;

    @Test
    void paginaDevuelveContenidoYTotales() {
        Project p1 = project(500L, "Uno");
        Project p2 = project(501L, "Dos");
        when(projectPersistence.findPageByPortfolioId(PORTFOLIO_ID, 2, 0)).thenReturn(Flux.just(p1, p2));
        when(projectPersistence.countByPortfolioId(PORTFOLIO_ID)).thenReturn(Mono.just(5L));
        when(projectTechnologyPersistence.findTechnologyIdsByProjectId(500L)).thenReturn(Flux.empty());
        when(projectTechnologyPersistence.findTechnologyIdsByProjectId(501L)).thenReturn(Flux.empty());

        StepVerifier.create(assembler.pageByPortfolio(PORTFOLIO_ID, 0, 2))
                .assertNext(page -> {
                    assertThat(page.page()).isZero();
                    assertThat(page.size()).isEqualTo(2);
                    assertThat(page.content()).hasSize(2);
                    assertThat(page.content().get(0).title()).isEqualTo("Uno");
                    assertThat(page.totalElements()).isEqualTo(5L);
                    assertThat(page.totalPages()).isEqualTo(3);
                })
                .verifyComplete();

        verify(catalogPersistence, never()).findByIds(any());
    }

    @Test
    void ultimaPaginaDevuelveContenidoParcialYTotalCorrecto() {
        Project p5 = project(505L, "Quinto");
        when(projectPersistence.findPageByPortfolioId(PORTFOLIO_ID, 2, 4)).thenReturn(Flux.just(p5));
        when(projectPersistence.countByPortfolioId(PORTFOLIO_ID)).thenReturn(Mono.just(5L));
        when(projectTechnologyPersistence.findTechnologyIdsByProjectId(505L)).thenReturn(Flux.empty());

        StepVerifier.create(assembler.pageByPortfolio(PORTFOLIO_ID, 2, 2))
                .assertNext(page -> {
                    assertThat(page.content()).hasSize(1);
                    assertThat(page.totalElements()).isEqualTo(5L);
                    assertThat(page.totalPages()).isEqualTo(3);
                })
                .verifyComplete();
    }

    @Test
    void paginaNegativaRechazada() {
        StepVerifier.create(pagina(-1, 20))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void sizeFueraDeRangoRechazado() {
        StepVerifier.create(pagina(0, 0))
                .expectError(IllegalArgumentException.class)
                .verify();
        StepVerifier.create(pagina(0, 101))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Mono<PageResponse> pagina(int page, int size) {
        return Mono.defer(() -> Mono.just(assembler.pageByPortfolio(PORTFOLIO_ID, page, size)))
                .flatMap(Function.identity());
    }

    private Project project(Long id, String title) {
        return Project.builder()
                .id(id).portfolioId(PORTFOLIO_ID).title(title).createdAt(Instant.now()).build();
    }
}