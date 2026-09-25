package com.bpao.devfoliobuilderapi.infrastructure.adapter.in.web;

import com.bpao.devfoliobuilderapi.application.dto.auth.AuthRequest;
import com.bpao.devfoliobuilderapi.application.dto.auth.RegisterRequest;
import com.bpao.devfoliobuilderapi.application.dto.auth.TokenResponse;
import com.bpao.devfoliobuilderapi.application.port.in.auth.AuthenticateUserUseCase;
import com.bpao.devfoliobuilderapi.application.port.in.auth.RegisterUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Endpoints publicos de autenticacion: el unico lugar del API donde se crea
 * un token. El resto del API sigue exigiendo Authorization: Bearer.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final AuthenticateUserUseCase authenticateUserUseCase;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TokenResponse> register(@RequestBody RegisterRequest request) {
        return registerUserUseCase.register(request);
    }

    @PostMapping("/login")
    public Mono<TokenResponse> login(@RequestBody AuthRequest request) {
        return authenticateUserUseCase.authenticate(request);
    }
}
