package com.bpao.devfoliobuilderapi.domain.exception;

/**
 * Credenciales rechazadas. Es un 401 y no un 422 porque el problema no esta en
 * el cuerpo de la peticion sino en quien intenta autenticarse.
 */
public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
