package com.bpao.devfoliobuilderapi.application.dto.portfolio;

import com.bpao.devfoliobuilderapi.domain.model.PortfolioBlock;

public final class BlockMapper {

    private BlockMapper() {
    }

    public static BlockResponse toResponse(PortfolioBlock block) {
        return new BlockResponse(
                block.getId(), block.getType(), block.getPosition(), block.getSettings());
    }
}