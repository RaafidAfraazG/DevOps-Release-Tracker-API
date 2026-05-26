package com.devops.releasetracker.mapper;

import com.devops.releasetracker.dto.UserResponse;
import com.devops.releasetracker.entity.User;

public final class UserMapper {
    private UserMapper() {
    }

    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
