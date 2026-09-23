package com.bpao.devfoliobuilderapi.application.dto.portfolio;

/**
 * Respuesta de la API para un bloque del portfolio.
 */
public record BlockResponse(Long id, String type, int position, String settings) {
}