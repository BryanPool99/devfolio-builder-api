package com.bpao.devfoliobuilderapi.application.dto.portfolio;

import com.bpao.devfoliobuilderapi.domain.model.Portfolio;

public final class PortfolioMapper {

    private PortfolioMapper() {
    }

    public static PortfolioResponse toResponse(Portfolio portfolio) {
        return new PortfolioResponse(portfolio.getId(), portfolio.getTitle());
    }
}