package com.bpao.devfoliobuilderapi.application.dto.user;

import com.bpao.devfoliobuilderapi.domain.model.Portfolio;
import com.bpao.devfoliobuilderapi.domain.model.User;

/**
 * Resultado de la auto-sincronizacion: usuario y su portfolio asociado.
 */
public record SyncResult(User user, Portfolio portfolio) {
}