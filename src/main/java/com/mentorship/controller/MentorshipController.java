package com.mentorship.controller;

import com.mentorship.dto.AcceptInviteRequest;
import com.mentorship.dto.InviteRequest;
import com.mentorship.dto.InvitationResponse;
import com.mentorship.dto.MentorshipResponse;
import com.mentorship.service.InvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/mentorship")
@RequiredArgsConstructor
@Tag(name = "Mentorship", description = "Manage mentorship invitations and active relationships")
public class MentorshipController {

  private final InvitationService invitationService;

  @PostMapping("/invite")
  @Operation(
          summary = "Send mentorship invite",
          description = "Mentor provides the student email and receives a pending invitation token."
  )
  public ResponseEntity<InvitationResponse> inviteStudent(@Valid @RequestBody InviteRequest request) {
    InvitationResponse invitation = invitationService.createInvitation(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(invitation);
  }

  @PostMapping("/accept")
  @Operation(
          summary = "Accept mentorship invite",
          description = "Student confirms the invitation using the token delivered to their email."
  )
  public ResponseEntity<MentorshipResponse> acceptInvitation(@Valid @RequestBody AcceptInviteRequest request) {
    MentorshipResponse mentorship = invitationService.acceptInvitation(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(mentorship);
  }

  @GetMapping("/mentor/{mentorId}/invitations")
  @Operation(
          summary = "List mentor invitations",
          description = "Retrieve pending and accepted invitations created by the specified mentor."
  )
  public ResponseEntity<List<InvitationResponse>> getMentorInvitations(@PathVariable UUID mentorId) {
    return ResponseEntity.ok(invitationService.getInvitationsForMentor(mentorId));
  }

  @GetMapping("/invitations/token/{token}")
  @Operation(
          summary = "Lookup invitation by token",
          description = "Fetch an invitation using the unique token that was sent to a student."
  )
  public ResponseEntity<InvitationResponse> getInvitationByToken(@PathVariable String token) {
    return invitationService.getInvitationByToken(token)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/mentor/{mentorId}/connections")
  @Operation(
          summary = "List mentor connections",
          description = "Return the students currently linked to the mentor through accepted invitations."
  )
  public ResponseEntity<List<MentorshipResponse>> getMentorConnections(@PathVariable UUID mentorId) {
    return ResponseEntity.ok(invitationService.getMentorshipsForMentor(mentorId));
  }

  @GetMapping("/student/{studentId}/connection")
  @Operation(
          summary = "Get student mentorship",
          description = "Retrieve the mentor attached to the student, if an invitation has been accepted."
  )
  public ResponseEntity<MentorshipResponse> getStudentConnection(@PathVariable UUID studentId) {
    return invitationService.getMentorshipForStudent(studentId)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
