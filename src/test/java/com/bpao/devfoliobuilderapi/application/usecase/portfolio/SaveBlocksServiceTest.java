package com.bpao.devfoliobuilderapi.application.usecase.portfolio;

import com.bpao.devfoliobuilderapi.application.dto.AuthenticatedUser;
import com.bpao.devfoliobuilderapi.application.dto.portfolio.BlockRequest;
import com.bpao.devfoliobuilderapi.application.dto.user.SyncResult;
import com.bpao.devfoliobuilderapi.application.port.in.user.SyncUserUseCase;
import com.bpao.devfoliobuilderapi.application.port.out.portfolio.PortfolioBlockPersistencePort;
import com.bpao.devfoliobuilderapi.domain.exception.DomainException;
import com.bpao.devfoliobuilderapi.domain.model.Portfolio;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaveBlocksServiceTest {

    private static final long PORTFOLIO_ID = 10L;

    @Mock
    private SyncUserUseCase syncUserUseCase;
    @Mock
    private PortfolioBlockPersistencePort portfolioBlockPersistence;
    @Mock
    private TransactionalOperator transactionalOperator;

    @InjectMocks
    private SaveBlocksService service;

    private final AuthenticatedUser principal = new AuthenticatedUser("auth-1", "juan@correo.com", "JuanPerez");

    @Test
    void saveBorraBloquesYReinsertaConPosicionesSecuenciales() {
        withPortfolio();
        when(portfolioBlockPersistence.deleteAllByPortfolioId(PORTFOLIO_ID)).thenReturn(Mono.empty());
        when(portfolioBlockPersistence.saveAll(anyList())).thenAnswer(invocation ->
                Flux.fromIterable(invocation.getArgument(0)));
        passthroughTransaction();

        StepVerifier.create(service.save(principal, List.of(
                        new BlockRequest("HERO", "{}"),
                        new BlockRequest("PROJECTS", "{\"order\":[1,2]}"))))
                .assertNext(block -> {
                    assertThat(block.type()).isEqualTo("HERO");
                    assertThat(block.position()).isZero();
                    assertThat(block.settings()).isEqualTo("{}");
                })
                .assertNext(block -> {
                    assertThat(block.type()).isEqualTo("PROJECTS");
                    assertThat(block.position()).isEqualTo(1);
                })
                .verifyComplete();

        verify(portfolioBlockPersistence).deleteAllByPortfolioId(PORTFOLIO_ID);
        verify(portfolioBlockPersistence).saveAll(anyList());
    }

    @Test
    void saveSinSettingsUsaVacio() {
        withPortfolio();
        when(portfolioBlockPersistence.deleteAllByPortfolioId(PORTFOLIO_ID)).thenReturn(Mono.empty());
        when(portfolioBlockPersistence.saveAll(anyList())).thenAnswer(invocation ->
                Flux.fromIterable(invocation.getArgument(0)));
        passthroughTransaction();

        StepVerifier.create(service.save(principal, List.of(new BlockRequest("HERO", null))))
                .assertNext(block -> assertThat(block.settings()).isEqualTo("{}"))
                .verifyComplete();
    }

    @Test
    void saveConTipoVacioDevuelveErrorDeDominio() {
        withPortfolio();

        StepVerifier.create(service.save(principal, List.of(new BlockRequest("", "{}"))))
                .expectError(DomainException.class)
                .verify();

        verify(portfolioBlockPersistence, never()).saveAll(anyList());
    }

    private void withPortfolio() {
        User user = User.builder().id(1L).authId("auth-1").username("juanperez").email("juan@correo.com").build();
        Portfolio portfolio = Portfolio.builder().id(PORTFOLIO_ID).userId(1L).title("Mi Portafolio").build();
        when(syncUserUseCase.sync(principal)).thenReturn(Mono.just(new SyncResult(user, portfolio)));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void passthroughTransaction() {
        when(transactionalOperator.transactional(any(Flux.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }
}