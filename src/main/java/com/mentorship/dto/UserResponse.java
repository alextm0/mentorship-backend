package com.mentorship.dto;

import com.mentorship.domain.User;
import com.mentorship.domain.UserRole;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        UserRole role,
        Instant createdAt
) {

  public static UserResponse from(User user) {
    User safeUser = Objects.requireNonNull(user, "user must not be null");
    return new UserResponse(
            safeUser.getId(),
            safeUser.getName(),
            safeUser.getEmail(),
            safeUser.getRole(),
            safeUser.getCreatedAt()
    );
  }
}
