package com.mentorship.dto;

import com.mentorship.domain.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UserCreateRequest(
        @NotNull(message = "ID cannot be null")
        UUID id,

        @NotBlank(message = "Name cannot be blank")
        String name,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Must be a valid email format")
        String email,

        @NotNull(message = "Role cannot be null")
        UserRole role
) {}