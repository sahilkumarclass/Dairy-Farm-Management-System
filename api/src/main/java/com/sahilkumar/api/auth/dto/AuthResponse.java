package com.sahilkumar.api.auth.dto;

import com.sahilkumar.api.auth.Role;
import java.util.UUID;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        UUID userId,
        String username,
        String fullName,
        Role role
) {}
