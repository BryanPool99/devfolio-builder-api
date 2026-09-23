package com.bpao.devfoliobuilderapi.application.usecase.catalog;

import com.bpao.devfoliobuilderapi.application.dto.catalog.CategoryResponse;
import com.bpao.devfoliobuilderapi.application.dto.catalog.TechnologyCatalogItem;
import com.bpao.devfoliobuilderapi.application.dto.catalog.TechnologyResponse;
import com.bpao.devfoliobuilderapi.application.port.out.catalog.CatalogPersistencePort;
import com.bpao.devfoliobuilderapi.domain.model.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    private CatalogPersistencePort catalogPersistence;

    @InjectMocks
    private CatalogService service;

    @Test
    void devuelveTodasLasCategorias() {
        when(catalogPersistence.findCategories())
                .thenReturn(Flux.just(new Category(1L, "Backend"), new Category(2L, "Frontend")));

        StepVerifier.create(service.getCategories())
                .expectNext(new CategoryResponse(1L, "Backend"), new CategoryResponse(2L, "Frontend"))
                .verifyComplete();
    }

    @Test
    void devuelveTodasLasTecnologias() {
        when(catalogPersistence.findTechnologies())
                .thenReturn(Flux.just(new TechnologyCatalogItem(1L, "Java", "/icons/java.svg", 1L, "Backend")));

        StepVerifier.create(service.getTechnologies(null))
                .expectNext(new TechnologyResponse(1L, "Java", "/icons/java.svg", 1L, "Backend"))
                .verifyComplete();
    }

    @Test
    void filtraTecnologiasPorCategoria() {
        when(catalogPersistence.findTechnologiesByCategory(9L))
                .thenReturn(Flux.just(new TechnologyCatalogItem(2L, "React", "/icons/react.svg", 9L, "Frontend")));

        StepVerifier.create(service.getTechnologies(9L))
                .expectNext(new TechnologyResponse(2L, "React", "/icons/react.svg", 9L, "Frontend"))
                .verifyComplete();
    }
}