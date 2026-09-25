package com.bpao.devfoliobuilderapi.application.port.out.security;

import com.bpao.devfoliobuilderapi.domain.model.User;

/**
 * Puerto de salida: emision del token de acceso. La implementacion real vive en
 * la infraestructura (JWT firmado con RS256).
 */
public interface TokenIssuerPort {

    IssuedToken issue(User user);

    /**
     * Token emitido junto con su vigencia en segundos, que el adaptador in
     * necesita para construir la respuesta.
     */
    record IssuedToken(String value, long expiresInSeconds) {
    }
}
