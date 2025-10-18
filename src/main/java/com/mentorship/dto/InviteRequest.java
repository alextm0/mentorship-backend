package com.mentorship.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record InviteRequest(
        @NotNull UUID mentorId,
        @NotBlank @Email String studentEmail
) {}
