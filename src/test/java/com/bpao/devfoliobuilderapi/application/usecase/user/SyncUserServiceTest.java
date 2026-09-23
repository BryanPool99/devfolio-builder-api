package com.bpao.devfoliobuilderapi.application.usecase.user;

import com.bpao.devfoliobuilderapi.application.dto.AuthenticatedUser;
import com.bpao.devfoliobuilderapi.application.dto.user.SyncResult;
import com.bpao.devfoliobuilderapi.application.port.out.portfolio.PortfolioBlockPersistencePort;
import com.bpao.devfoliobuilderapi.application.port.out.portfolio.PortfolioPersistencePort;
import com.bpao.devfoliobuilderapi.application.port.out.user.UserPersistencePort;
import com.bpao.devfoliobuilderapi.domain.model.Portfolio;
import com.bpao.devfoliobuilderapi.domain.model.PortfolioBlock;
import com.bpao.devfoliobuilderapi.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SyncUserServiceTest {

    private static final String AUTH_ID = "auth-123";

    @Mock
    private UserPersistencePort userPersistence;
    @Mock
    private PortfolioPersistencePort portfolioPersistence;
    @Mock
    private PortfolioBlockPersistencePort portfolioBlockPersistence;
    @Mock
    private TransactionalOperator transactionalOperator;

    @InjectMocks
    private SyncUserService service;

    private final AuthenticatedUser principal =
            new AuthenticatedUser(AUTH_ID, "juan@correo.com", "JuanPerez");

    @Test
    void primerJwtCreaUsuarioPortfolioYBloqueHero() {
        when(userPersistence.findByAuthId(AUTH_ID)).thenReturn(Mono.empty());
        User savedUser = User.builder()
                .id(1L).authId(AUTH_ID).username("juanperez").email("juan@correo.com")
                .createdAt(Instant.now()).build();
        when(userPersistence.save(any(User.class))).thenReturn(Mono.just(savedUser));

        when(portfolioPersistence.findByUserId(1L)).thenReturn(Mono.empty());
        Portfolio savedPortfolio = Portfolio.builder()
                .id(10L).userId(1L).title(Portfolio.DEFAULT_TITLE).published(true).build();
        when(portfolioPersistence.save(any(Portfolio.class))).thenReturn(Mono.just(savedPortfolio));
        when(portfolioBlockPersistence.save(any(PortfolioBlock.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0, PortfolioBlock.class)));
        passthroughTransaction();

        StepVerifier.create(service.sync(principal))
                .assertNext(result -> {
                    assertThat(result.user().getId()).isEqualTo(1L);
                    assertThat(result.portfolio().getId()).isEqualTo(10L);
                })
                .verifyComplete();

        verify(userPersistence).save(User.create(AUTH_ID, "juanperez", "juan@correo.com"));
        verify(portfolioPersistence).save(any(Portfolio.class));
        verify(portfolioBlockPersistence).save(argThat(block -> block.getType().equals("HERO")
                && block.getPosition() == 0
                && block.getPortfolioId().equals(10L)));
    }

    @Test
    void usuarioYPortfolioExistentesNoPersistenNada() {
        User existing = User.builder()
                .id(1L).authId(AUTH_ID).username("juanperez").email("juan@correo.com")
                .createdAt(Instant.now()).build();
        Portfolio existingPortfolio = Portfolio.builder()
                .id(10L).userId(1L).title(Portfolio.DEFAULT_TITLE).published(true).build();
        when(userPersistence.findByAuthId(AUTH_ID)).thenReturn(Mono.just(existing));
        when(portfolioPersistence.findByUserId(1L)).thenReturn(Mono.just(existingPortfolio));

        StepVerifier.create(service.sync(principal))
                .expectNext(new SyncResult(existing, existingPortfolio))
                .verifyComplete();

        verify(userPersistence, never()).save(any());
        verify(portfolioPersistence, never()).save(any());
        verify(portfolioBlockPersistence, never()).save(any());
    }

    @Test
    void usuarioExistenteSinPortfolioCreaSoloPortfolio() {
        User existing = User.builder()
                .id(1L).authId(AUTH_ID).username("juanperez").email("juan@correo.com")
                .createdAt(Instant.now()).build();
        when(userPersistence.findByAuthId(AUTH_ID)).thenReturn(Mono.just(existing));
        when(portfolioPersistence.findByUserId(1L)).thenReturn(Mono.empty());
        Portfolio savedPortfolio = Portfolio.builder()
                .id(20L).userId(1L).title(Portfolio.DEFAULT_TITLE).published(true).build();
        when(portfolioPersistence.save(any(Portfolio.class))).thenReturn(Mono.just(savedPortfolio));
        when(portfolioBlockPersistence.save(any(PortfolioBlock.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0, PortfolioBlock.class)));
        passthroughTransaction();

        StepVerifier.create(service.sync(principal))
                .assertNext(result -> assertThat(result.portfolio().getId()).isEqualTo(20L))
                .verifyComplete();

        verify(userPersistence, never()).save(any());
        verify(portfolioPersistence).save(any(Portfolio.class));
        verify(portfolioBlockPersistence).save(any(PortfolioBlock.class));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void passthroughTransaction() {
        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }
}