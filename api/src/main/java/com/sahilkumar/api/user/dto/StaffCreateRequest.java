package com.sahilkumar.api.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record StaffCreateRequest(
        @NotBlank @Size(min = 3, max = 80) String username,
        @NotBlank @Size(min = 6, max = 100) String password,
        @NotBlank @Size(max = 120) String fullName,
        @Pattern(regexp = "^$|^\\d{10}$", message = "Phone must be exactly 10 digits") String phone
) {}
