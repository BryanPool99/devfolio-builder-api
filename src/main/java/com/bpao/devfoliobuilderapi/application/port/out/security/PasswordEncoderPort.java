package com.bpao.devfoliobuilderapi.application.port.out.security;

/**
 * Puerto de salida: cifrado de contraseñas. La implementacion real vive en la
 * infraestructura (BCrypt); el hexágono solo conoce esta interfaz.
 */
public interface PasswordEncoderPort {

    String hash(String rawPassword);

    boolean matches(String rawPassword, String passwordHash);
}
