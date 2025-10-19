package com.mentorship.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AcceptInviteRequest(
    @NotEmpty String token,
    @NotNull UUID studentId
) {}
