package com.bpao.devfoliobuilderapi.application.usecase.portfolio;

import com.bpao.devfoliobuilderapi.application.dto.AuthenticatedUser;
import com.bpao.devfoliobuilderapi.application.dto.user.SyncResult;
import com.bpao.devfoliobuilderapi.application.port.in.user.SyncUserUseCase;
import com.bpao.devfoliobuilderapi.application.port.out.portfolio.PortfolioBlockPersistencePort;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetBlocksServiceTest {

    private static final long PORTFOLIO_ID = 10L;

    @Mock
    private SyncUserUseCase syncUserUseCase;
    @Mock
    private PortfolioBlockPersistencePort portfolioBlockPersistence;

    @InjectMocks
    private GetBlocksService service;

    private final AuthenticatedUser principal = new AuthenticatedUser("auth-1", "juan@correo.com", "JuanPerez");

    @Test
    void getBlocksDevuelveBloquesMapeadosAResponse() {
        withPortfolio();
        when(portfolioBlockPersistence.findByPortfolioId(PORTFOLIO_ID)).thenReturn(Flux.just(
                block(1L, "HERO", 0, "{}"),
                block(2L, "PROJECTS", 1, "{\"limit\":6}")));

        StepVerifier.create(service.getBlocks(principal))
                .assertNext(response -> {
                    assertThat(response.id()).isEqualTo(1L);
                    assertThat(response.type()).isEqualTo("HERO");
                    assertThat(response.position()).isZero();
                    assertThat(response.settings()).isEqualTo("{}");
                })
                .assertNext(response -> {
                    assertThat(response.type()).isEqualTo("PROJECTS");
                    assertThat(response.position()).isEqualTo(1);
                    assertThat(response.settings()).isEqualTo("{\"limit\":6}");
                })
                .verifyComplete();

        verify(syncUserUseCase).sync(principal);
        verify(portfolioBlockPersistence).findByPortfolioId(PORTFOLIO_ID);
    }

    @Test
    void getBlocksSinBloquesDevuelveFluxVacio() {
        withPortfolio();
        when(portfolioBlockPersistence.findByPortfolioId(PORTFOLIO_ID)).thenReturn(Flux.empty());

        StepVerifier.create(service.getBlocks(principal))
                .verifyComplete();
    }

    private void withPortfolio() {
        User user = User.builder().id(1L).authId("auth-1").username("juanperez").email("juan@correo.com").build();
        Portfolio portfolio = Portfolio.builder().id(PORTFOLIO_ID).userId(1L).title("Mi Portafolio").build();
        when(syncUserUseCase.sync(principal)).thenReturn(Mono.just(new SyncResult(user, portfolio)));
    }

    private PortfolioBlock block(Long id, String type, int position, String settings) {
        return PortfolioBlock.builder()
                .id(id)
                .portfolioId(PORTFOLIO_ID)
                .type(type)
                .position(position)
                .settings(settings)
                .build();
    }
}
