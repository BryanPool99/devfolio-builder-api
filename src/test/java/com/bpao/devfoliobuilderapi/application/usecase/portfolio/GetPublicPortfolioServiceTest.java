package com.bpao.devfoliobuilderapi.application.usecase.portfolio;

import com.bpao.devfoliobuilderapi.application.dto.common.PageResponse;
import com.bpao.devfoliobuilderapi.application.dto.portfolio.BlockResponse;
import com.bpao.devfoliobuilderapi.application.dto.portfolio.PublicPortfolioResponse;
import com.bpao.devfoliobuilderapi.application.dto.project.ProjectResponse;
import com.bpao.devfoliobuilderapi.application.port.out.portfolio.PortfolioBlockPersistencePort;
import com.bpao.devfoliobuilderapi.application.port.out.portfolio.PortfolioPersistencePort;
import com.bpao.devfoliobuilderapi.application.port.out.user.UserPersistencePort;
import com.bpao.devfoliobuilderapi.application.usecase.project.ProjectResponseAssembler;
import com.bpao.devfoliobuilderapi.domain.exception.NotFoundException;
import com.bpao.devfoliobuilderapi.domain.model.Portfolio;
import com.bpao.devfoliobuilderapi.domain.model.PortfolioBlock;
import com.bpao.devfoliobuilderapi.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetPublicPortfolioServiceTest {

    @Mock
    private UserPersistencePort userPersistence;
    @Mock
    private PortfolioPersistencePort portfolioPersistence;
    @Mock
    private PortfolioBlockPersistencePort portfolioBlockPersistence;
    @Mock
    private ProjectResponseAssembler projectResponseAssembler;

    @InjectMocks
    private GetPublicPortfolioService service;

    @Test
    void devuelvePortfolioPublicoConBloquesYProyectosPaginados() {
        User user = User.builder().id(1L).authId("auth-1").username("juanperez").email("juan@correo.com").build();
        Portfolio portfolio = Portfolio.builder().id(10L).userId(1L).title("Mi Portafolio").published(true).build();
        when(userPersistence.findByUsername("juanperez")).thenReturn(Mono.just(user));
        when(portfolioPersistence.findByUserId(1L)).thenReturn(Mono.just(portfolio));
        when(portfolioBlockPersistence.findByPortfolioId(10L)).thenReturn(Flux.just(
                PortfolioBlock.builder().id(1L).portfolioId(10L).type("HERO").position(0).settings("{}").build(),
                PortfolioBlock.builder().id(2L).portfolioId(10L).type("PROJECTS").position(1).settings("{}").build()));
        PageResponse<ProjectResponse> projectsPage = PageResponse.of(List.of(
                new ProjectResponse(50L, "Mi app", null, null, null, null, null, List.of())), 0, 20, 1L);
        when(projectResponseAssembler.pageByPortfolio(10L, 0, 20)).thenReturn(Mono.just(projectsPage));

        StepVerifier.create(service.getByUsername("juanperez", 0, 20))
                .assertNext(response -> {
                    assertThat(response.username()).isEqualTo("juanperez");
                    assertThat(response.title()).isEqualTo("Mi Portafolio");
                    assertThat(response.published()).isTrue();
                    assertThat(response.blocks()).extracting(BlockResponse::type)
                            .containsExactly("HERO", "PROJECTS");
                    assertThat(response.projects().content()).hasSize(1);
                    assertThat(response.projects().content().get(0).title()).isEqualTo("Mi app");
                    assertThat(response.projects().totalElements()).isEqualTo(1L);
                })
                .verifyComplete();
    }

    @Test
    void paginaLosProyectosConLosParametrosRecibidos() {
        User user = User.builder().id(1L).authId("auth-1").username("juanperez").email("juan@correo.com").build();
        Portfolio portfolio = Portfolio.builder().id(10L).userId(1L).title("Mi Portafolio").published(true).build();
        when(userPersistence.findByUsername("juanperez")).thenReturn(Mono.just(user));
        when(portfolioPersistence.findByUserId(1L)).thenReturn(Mono.just(portfolio));
        when(portfolioBlockPersistence.findByPortfolioId(10L)).thenReturn(Flux.empty());
        when(projectResponseAssembler.pageByPortfolio(10L, 1, 5))
                .thenReturn(Mono.just(PageResponse.of(List.of(), 1, 5, 0L)));

        StepVerifier.create(service.getByUsername("juanperez", 1, 5))
                .expectNextCount(1)
                .verifyComplete();

        verify(projectResponseAssembler).pageByPortfolio(eq(10L), eq(1), eq(5));
    }

    @Test
    void usuarioInexistenteDevuelve404() {
        when(userPersistence.findByUsername("nadie")).thenReturn(Mono.empty());

        StepVerifier.create(service.getByUsername("nadie", 0, 20))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(NotFoundException.class);
                    assertThat(error.getMessage()).contains("nadie");
                })
                .verify();

        verify(portfolioPersistence, never()).findByUserId(any());
    }

    @Test
    void portfolioNoPublicadoDevuelve404() {
        User user = User.builder().id(1L).authId("auth-1").username("juanperez").email("juan@correo.com").build();
        Portfolio unpublished = Portfolio.builder().id(10L).userId(1L).title("Oculto").published(false).build();
        when(userPersistence.findByUsername("juanperez")).thenReturn(Mono.just(user));
        when(portfolioPersistence.findByUserId(1L)).thenReturn(Mono.just(unpublished));

        StepVerifier.create(service.getByUsername("juanperez", 0, 20))
                .expectError(NotFoundException.class)
                .verify();

        verify(portfolioBlockPersistence, never()).findByPortfolioId(10L);
    }

    @Test
    void usuarioSinPortfolioDevuelve404() {
        User user = User.builder().id(1L).authId("auth-1").username("juanperez").email("juan@correo.com").build();
        when(userPersistence.findByUsername("juanperez")).thenReturn(Mono.just(user));
        when(portfolioPersistence.findByUserId(1L)).thenReturn(Mono.empty());

        StepVerifier.create(service.getByUsername("juanperez", 0, 20))
                .expectError(NotFoundException.class)
                .verify();
    }
}