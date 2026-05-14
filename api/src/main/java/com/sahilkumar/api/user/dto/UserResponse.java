package com.sahilkumar.api.user.dto;

import com.sahilkumar.api.auth.Role;
import com.sahilkumar.api.auth.User;
import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String fullName,
        String phone,
        Role role,
        boolean enabled,
        Instant createdAt
) {
    public static UserResponse from(User u) {
        return new UserResponse(
                u.getId(), u.getUsername(), u.getFullName(), u.getPhone(),
                u.getRole(), u.isEnabled(), u.getCreatedAt());
    }
}
