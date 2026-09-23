package com.bpao.devfoliobuilderapi.infrastructure.adapter.in.web;

import com.bpao.devfoliobuilderapi.application.dto.AuthenticatedUser;
import com.bpao.devfoliobuilderapi.application.dto.portfolio.BlockRequest;
import com.bpao.devfoliobuilderapi.application.dto.portfolio.BlockResponse;
import com.bpao.devfoliobuilderapi.application.port.in.portfolio.SaveBlocksUseCase;
import com.bpao.devfoliobuilderapi.infrastructure.security.AuthenticatedUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Guardar/reordenar los bloques del portfolio del usuario autenticado.
 */
@RestController
@RequestMapping("/api/v1/portfolio/blocks")
@RequiredArgsConstructor
public class PortfolioBlockController {

    private final SaveBlocksUseCase saveBlocksUseCase;

    @PutMapping
    public Flux<BlockResponse> saveBlocks(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody List<BlockRequest> blocks) {
        return saveBlocksUseCase.save(AuthenticatedUserMapper.from(jwt), blocks);
    }
}