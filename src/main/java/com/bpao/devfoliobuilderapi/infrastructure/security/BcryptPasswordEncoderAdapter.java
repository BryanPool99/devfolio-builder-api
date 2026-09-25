package com.bpao.devfoliobuilderapi.infrastructure.security;

import com.bpao.devfoliobuilderapi.application.port.out.security.PasswordEncoderPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Cifrado de contrasenas con BCrypt. El hash ocupa 60 caracteres, por eso la
 * columna password_hash es VARCHAR(100).
 */
@Component
public class BcryptPasswordEncoderAdapter implements PasswordEncoderPort {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String hash(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String passwordHash) {
        return encoder.matches(rawPassword, passwordHash);
    }
}
