package com.bpao.devfoliobuilderapi.domain.exception;

/**
 * Recurso no encontrado (HTTP 404). Se diferencia de DomainException (422).
 */
public class NotFoundException extends DomainException {

    public NotFoundException(String message) {
        super(message);
    }
}