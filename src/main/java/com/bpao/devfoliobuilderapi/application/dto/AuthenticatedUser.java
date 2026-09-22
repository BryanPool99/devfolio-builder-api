package com.bpao.devfoliobuilderapi.application.dto;

import java.time.Instant;

/**
 * Principal autenticado extraido del JWT de Neon Auth (adapter in -> application).
 * Nunca contiene tipos de Spring Security para no acoplar el hexagono.
 */
public record AuthenticatedUser(String authId, String email, String preferredUsername) {
}