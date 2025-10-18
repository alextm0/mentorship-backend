package com.mentorship.dto;

import com.mentorship.domain.Invitation;
import com.mentorship.domain.InvitationStatus;

import java.time.Instant;
import java.util.UUID;

public record InvitationResponse(
        UUID id,
        UUID mentorId,
        String studentEmail,
        InvitationStatus status,
        String token,
        Instant createdAt
) {

  public static InvitationResponse from(Invitation invitation) {
    return new InvitationResponse(
            invitation.getId(),
            invitation.getMentor().getId(),
            invitation.getStudentEmail(),
            invitation.getStatus(),
            invitation.getToken(),
            invitation.getCreatedAt()
    );
  }
}
