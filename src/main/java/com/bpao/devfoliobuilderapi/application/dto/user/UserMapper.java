package com.bpao.devfoliobuilderapi.application.dto.user;

import com.bpao.devfoliobuilderapi.domain.model.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getCreatedAt());
    }
}