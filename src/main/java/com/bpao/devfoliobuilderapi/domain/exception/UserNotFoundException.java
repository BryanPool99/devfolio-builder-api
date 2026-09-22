package com.bpao.devfoliobuilderapi.domain.exception;

public class UserNotFoundException extends DomainException {

    public UserNotFoundException(String identifier) {
        super("Usuario no encontrado: " + identifier);
    }
}