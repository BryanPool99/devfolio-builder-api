package com.bpao.devfoliobuilderapi.infrastructure.adapter.in.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint publico del portafolio (/p/{username}).
 * Placeholder: la maquetacion del portafolio publico llega en una iteracion posterior.
 */
@Slf4j
@RestController
public class PublicPortfolioController {

    @GetMapping("/p/{username}")
    public ResponseEntity<ProblemDetail> publicPortfolio(@PathVariable String username) {
        log.debug("Peticion al portafolio publico de {}", username);
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(ProblemDetail.forStatusAndDetail(
                        HttpStatus.NOT_IMPLEMENTED,
                        "El portafolio publico aun no esta implementado"));
    }
}