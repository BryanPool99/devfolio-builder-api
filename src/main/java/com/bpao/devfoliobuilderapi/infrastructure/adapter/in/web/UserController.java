package com.bpao.devfoliobuilderapi.infrastructure.adapter.in.web;

import com.bpao.devfoliobuilderapi.application.dto.AuthenticatedUser;
import com.bpao.devfoliobuilderapi.application.dto.user.UserMapper;
import com.bpao.devfoliobuilderapi.application.dto.user.UserResponse;
import com.bpao.devfoliobuilderapi.application.port.in.user.GetCurrentUserUseCase;
import com.bpao.devfoliobuilderapi.infrastructure.security.AuthenticatedUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final GetCurrentUserUseCase getCurrentUserUseCase;

    @GetMapping("/me")
    public Mono<UserResponse> me(@AuthenticationPrincipal Jwt jwt) {
        AuthenticatedUser principal = AuthenticatedUserMapper.from(jwt);
        return getCurrentUserUseCase.findOrCreate(principal).map(UserMapper::toResponse);
    }
}