package com.bpao.devfoliobuilderapi.domain.model;

import com.bpao.devfoliobuilderapi.domain.exception.DomainException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;

/**
 * Entidad de dominio para la tabla users.
 * El authId corresponde al claim 'sub' del JWT emitido por Neon Auth.
 */
@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    private final Long id;
    private final String authId;
    private final String username;
    private final String email;
    private final Instant createdAt;

    public static User create(String authId, String username, String email) {
        requireText("authId", authId);
        requireText("username", username);
        requireText("email", email);
        return builder()
                .authId(authId)
                .username(username)
                .email(email)
                .build();
    }

    public User withId(Long newId) {
        return builder()
                .id(newId)
                .authId(authId)
                .username(username)
                .email(email)
                .createdAt(createdAt)
                .build();
    }

    private static void requireText(String field, String value) {
        if (value == null || value.isBlank()) {
            throw new DomainException(field + " no puede ser nulo ni vacio");
        }
    }
}