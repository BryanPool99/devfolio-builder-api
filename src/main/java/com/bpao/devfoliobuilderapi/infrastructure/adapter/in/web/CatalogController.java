package com.bpao.devfoliobuilderapi.infrastructure.adapter.in.web;

import com.bpao.devfoliobuilderapi.application.dto.catalog.CategoryResponse;
import com.bpao.devfoliobuilderapi.application.dto.catalog.TechnologyResponse;
import com.bpao.devfoliobuilderapi.application.port.in.catalog.GetCategoriesUseCase;
import com.bpao.devfoliobuilderapi.application.port.in.catalog.GetTechnologiesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CatalogController {

    private final GetCategoriesUseCase getCategoriesUseCase;
    private final GetTechnologiesUseCase getTechnologiesUseCase;

    @GetMapping("/categories")
    public Flux<CategoryResponse> categories() {
        return getCategoriesUseCase.getCategories();
    }

    @GetMapping("/technologies")
    public Flux<TechnologyResponse> technologies(@RequestParam(required = false) Long categoryId) {
        return getTechnologiesUseCase.getTechnologies(categoryId);
    }
}