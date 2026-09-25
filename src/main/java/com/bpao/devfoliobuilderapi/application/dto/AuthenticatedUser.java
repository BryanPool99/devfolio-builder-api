package com.bpao.devfoliobuilderapi.application.dto;

/**
 * Principal autenticado extraido del JWT emitido por este backend
 * (adapter in -> application). Nunca contiene tipos de Spring Security para no
 * acoplar el hexagono.
 */
public record AuthenticatedUser(String authId, String email, String preferredUsername) {
}