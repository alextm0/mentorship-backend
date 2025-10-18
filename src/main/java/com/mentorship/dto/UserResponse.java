package com.mentorship.dto;

import com.mentorship.domain.User; // Make sure this import is correct
import com.mentorship.domain.UserRole;
import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        UserRole role,
        Instant createdAt
) {
  // Helper method to convert an entity to a response DTO
  public static UserResponse from(User user) {
    return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            user.getCreatedAt() // Assumes your User entity has this
    );
  }
}