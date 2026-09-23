package com.bpao.devfoliobuilderapi.application.port.in.user;

import com.bpao.devfoliobuilderapi.application.dto.AuthenticatedUser;
import com.bpao.devfoliobuilderapi.application.dto.user.SyncResult;
import reactor.core.publisher.Mono;

/**
 * Puerto de entrada: auto-sincronizacion del usuario federado con Neon Auth.
 */
public interface SyncUserUseCase {

    /**
     * Registra (si es su primera vez) el usuario y su portfolio por defecto,
     * y devuelve ambos.
     */
    Mono<SyncResult> sync(AuthenticatedUser authenticatedUser);
}