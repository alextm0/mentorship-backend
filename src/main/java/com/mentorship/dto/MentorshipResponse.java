package com.mentorship.dto;

import com.mentorship.domain.Mentorship;

import java.time.Instant;
import java.util.UUID;

public record MentorshipResponse(
        UUID id,
        UUID mentorId,
        UUID studentId,
        Instant createdAt
) {

  public static MentorshipResponse from(Mentorship mentorship) {
    return new MentorshipResponse(
            mentorship.getId(),
            mentorship.getMentor().getId(),
            mentorship.getStudent().getId(),
            mentorship.getCreatedAt()
    );
  }
}
