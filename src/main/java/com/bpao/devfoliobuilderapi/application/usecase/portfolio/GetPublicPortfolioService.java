package com.bpao.devfoliobuilderapi.application.usecase.portfolio;

import com.bpao.devfoliobuilderapi.application.dto.portfolio.BlockMapper;
import com.bpao.devfoliobuilderapi.application.dto.portfolio.PublicPortfolioResponse;
import com.bpao.devfoliobuilderapi.application.port.in.portfolio.GetPublicPortfolioUseCase;
import com.bpao.devfoliobuilderapi.application.port.out.portfolio.PortfolioBlockPersistencePort;
import com.bpao.devfoliobuilderapi.application.port.out.portfolio.PortfolioPersistencePort;
import com.bpao.devfoliobuilderapi.application.port.out.user.UserPersistencePort;
import com.bpao.devfoliobuilderapi.application.usecase.project.ProjectResponseAssembler;
import com.bpao.devfoliobuilderapi.domain.exception.NotFoundException;
import com.bpao.devfoliobuilderapi.domain.model.Portfolio;
import com.bpao.devfoliobuilderapi.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Caso de uso: lectura publica del portafolio por username. Devuelve 404 si
 * el usuario no existe, si no tiene portfolio o si no esta publicado. Los
 * proyectos se devuelven paginados por offset/limit.
 */
@Service
@RequiredArgsConstructor
public class GetPublicPortfolioService implements GetPublicPortfolioUseCase {

    private final UserPersistencePort userPersistence;
    private final PortfolioPersistencePort portfolioPersistence;
    private final PortfolioBlockPersistencePort portfolioBlockPersistence;
    private final ProjectResponseAssembler projectResponseAssembler;

    @Override
    public Mono<PublicPortfolioResponse> getByUsername(String username, int page, int size) {
        return userPersistence.findByUsername(username)
                .switchIfEmpty(Mono.error(new NotFoundException("Portafolio no encontrado: " + username)))
                .flatMap(user -> portfolioPersistence.findByUserId(user.getId())
                        .filter(Portfolio::isPublished)
                        .switchIfEmpty(Mono.error(new NotFoundException("Portafolio no encontrado: " + username)))
                        .flatMap(portfolio -> assemble(user, portfolio, page, size)));
    }

    private Mono<PublicPortfolioResponse> assemble(User user, Portfolio portfolio, int page, int size) {
        return portfolioBlockPersistence.findByPortfolioId(portfolio.getId())
                .collectList()
                .flatMap(blocks -> projectResponseAssembler.pageByPortfolio(portfolio.getId(), page, size)
                        .map(projectsPage -> new PublicPortfolioResponse(
                                user.getUsername(),
                                portfolio.getTitle(),
                                portfolio.isPublished(),
                                blocks.stream().map(BlockMapper::toResponse).toList(),
                                projectsPage)));
    }
}