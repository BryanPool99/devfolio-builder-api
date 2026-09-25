package com.bpao.devfoliobuilderapi.domain.model;

import com.bpao.devfoliobuilderapi.domain.exception.DomainException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Entidad de dominio para la tabla users.
 * El authId es el identificador propio de la aplicacion: se genera en el registro
 * y se emite como claim 'sub' del JWT que firma el backend.
 */
@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-z0-9](?:[a-z0-9-]{1,28}[a-z0-9])$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 72;

    private final Long id;
    private final String authId;
    private final String username;
    private final String email;
    private final String passwordHash;
    private final UserRole role;
    private final boolean emailVerified;
    private final Instant createdAt;
    private final Instant updatedAt;

    public static User create(String authId, String username, String email) {
        requireText("authId", authId);
        requireText("username", username);
        requireText("email", email);
        return builder()
                .authId(authId)
                .username(username)
                .email(email)
                .role(UserRole.USER)
                .build();
    }

    /**
     * Valida el formato de las credenciales sin construir un usuario. Permite
     * rechazar una peticion invalida antes de tocar la base de datos.
     */
    public static void validateCredentials(String username, String email, String rawPassword) {
        normalizeUsername(username);
        normalizeEmail(email);
        requirePassword(rawPassword);
    }

    /**
     * Alta de usuario con credenciales propias. El passwordHash ya viene cifrado:
     * el dominio nunca ve la contraseña en claro.
     */
    public static User register(String authId, String username, String email, String rawPassword, String passwordHash) {
        requireText("authId", authId);
        requireText("passwordHash", passwordHash);
        String normalizedUsername = normalizeUsername(username);
        String normalizedEmail = normalizeEmail(email);
        requirePassword(rawPassword);
        return builder()
                .authId(authId)
                .username(normalizedUsername)
                .email(normalizedEmail)
                .passwordHash(passwordHash)
                .role(UserRole.USER)
                .emailVerified(false)
                .build();
    }

    public User withId(Long newId) {
        return builder()
                .id(newId)
                .authId(authId)
                .username(username)
                .email(email)
                .passwordHash(passwordHash)
                .role(role)
                .emailVerified(emailVerified)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    /**
     * Normaliza y valida el username. Es publico para que la aplicacion pueda
     * comprobar disponibilidad contra el mismo valor que se terminara guardando.
     */
    public static String normalizeUsername(String value) {
        requireText("username", value);
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        if (!USERNAME_PATTERN.matcher(normalized).matches()) {
            throw new DomainException(
                    "El username debe tener entre 3 y 30 caracteres, solo letras, digitos y guiones, "
                            + "y no puede empezar ni terminar con guion");
        }
        return normalized;
    }

    /**
     * Normaliza y valida el email. Es publico por el mismo motivo que
     * {@link #normalizeUsername(String)}.
     */
    public static String normalizeEmail(String value) {
        requireText("email", value);
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new DomainException("El email no tiene un formato valido");
        }
        return normalized;
    }

    private static void requirePassword(String value) {
        if (value == null || value.length() < MIN_PASSWORD_LENGTH || value.length() > MAX_PASSWORD_LENGTH) {
            // 72 es el limite que trunca BCrypt.
            throw new DomainException(
                    "La contraseña debe tener entre " + MIN_PASSWORD_LENGTH + " y " + MAX_PASSWORD_LENGTH + " caracteres");
        }
    }

    private static void requireText(String field, String value) {
        if (value == null || value.isBlank()) {
            throw new DomainException(field + " no puede ser nulo ni vacio");
        }
    }
}
