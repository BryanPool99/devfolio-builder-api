package com.bpao.devfoliobuilderapi.application.dto.portfolio;

/**
 * Cuerpo de PUT /api/v1/portfolio/blocks: el cliente envia la lista completa
 * de bloques en el orden deseado.
 */
public record BlockRequest(String type, String settings) {
}