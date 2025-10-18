package com.mentorship.service;

import com.mentorship.domain.Invitation;
import com.mentorship.domain.InvitationStatus;
import com.mentorship.domain.User;
import com.mentorship.domain.UserRole;
import com.mentorship.dto.AcceptInviteRequest;
import com.mentorship.dto.InviteRequest;
import com.mentorship.dto.InvitationResponse;
import com.mentorship.dto.MentorshipResponse;
import com.mentorship.exception.ConflictException;
import com.mentorship.exception.ForbiddenException;
import com.mentorship.repository.InvitationRepository;
import com.mentorship.repository.MentorshipRepository;
import com.mentorship.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@DisplayName("Invitation Workflow Integration Tests")
class InvitationWorkflowIntegrationTest {

  @Autowired private InvitationService invitationService;
  @Autowired private UserRepository userRepository;
  @Autowired private InvitationRepository invitationRepository;
  @Autowired private MentorshipRepository mentorshipRepository;

  private User mentor;
  private User student;

  @BeforeEach
  void setUp() {
    mentorshipRepository.deleteAll();
    invitationRepository.deleteAll();
    userRepository.deleteAll();

    mentor = userRepository.save(User.builder()
            .id(UUID.randomUUID())
            .name("Sarah Mentor")
            .email("sarah@example.com")
            .role(UserRole.MENTOR)
            .build());

    student = userRepository.save(User.builder()
            .id(UUID.randomUUID())
            .name("Alex Student")
            .email("alex@example.com")
            .role(UserRole.STUDENT)
            .build());
  }

  @Test
  @DisplayName("should complete the full invitation and acceptance flow successfully")
  void fullInvitationFlow_createsPendingInvitationAndConfirmedMentorship() {
    // Act 1: Create Invitation with email needing normalization
    InvitationResponse invitation = invitationService.createInvitation(
            new InviteRequest(mentor.getId(), "  Alex@example.com  ")
    );

    // Assert 1: Check the pending invitation
    assertThat(invitation.mentorId()).isEqualTo(mentor.getId());
    assertThat(invitation.studentEmail()).isEqualTo("alex@example.com");
    assertThat(invitation.status()).isEqualTo(InvitationStatus.PENDING);
    assertThat(invitation.token()).isNotBlank();

    // Act 2: Accept Invitation
    MentorshipResponse mentorship = invitationService.acceptInvitation(
            new AcceptInviteRequest(invitation.token(), student.getId())
    );

    // Assert 2: Check the final mentorship and side effects
    assertThat(mentorship.mentorId()).isEqualTo(mentor.getId());
    assertThat(mentorship.studentId()).isEqualTo(student.getId());
    assertThat(invitationRepository.findByToken(invitation.token())).get()
            .extracting(Invitation::getStatus).isEqualTo(InvitationStatus.ACCEPTED);
    assertThat(mentorshipRepository.findByStudent_Id(student.getId())).isPresent();
  }

  @Nested
  @DisplayName("When Creating an Invitation")
  class WhenCreatingInvitation {

    @Test
    @DisplayName("should throw exception when a duplicate pending invite exists")
    void preventsDuplicatePendingInvite() {
      invitationService.createInvitation(new InviteRequest(mentor.getId(), "alex@example.com"));

      assertThatThrownBy(() -> invitationService.createInvitation(new InviteRequest(mentor.getId(), "Alex@example.com")))
              .isInstanceOf(ConflictException.class)
              .hasMessageContaining("pending invite");
    }

    @Test
    @DisplayName("should throw exception when mentor invites themselves")
    void throwsException_whenMentorInvitesThemselves() {
      assertThatThrownBy(() -> invitationService.createInvitation(new InviteRequest(mentor.getId(), mentor.getEmail())))
              .isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("Mentors cannot invite themselves");
    }

    @Test
    @DisplayName("should throw exception when a student tries to send an invite")
    void throwsException_whenStudentTriesToInvite() {
      InviteRequest request = new InviteRequest(student.getId(), "another-student@example.com");

      assertThatThrownBy(() -> invitationService.createInvitation(request))
              .isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("Only mentors can send invitations");
    }
  }

  @Nested
  @DisplayName("When Accepting an Invitation")
  class WhenAcceptingInvitation {

    @Test
    @DisplayName("should throw exception when an imposter student tries to accept")
    void rejectsDifferentStudentEmail() {
      InvitationResponse invitation = invitationService.createInvitation(new InviteRequest(mentor.getId(), "alex@example.com"));
      User imposterStudent = userRepository.save(User.builder()
              .id(UUID.randomUUID()).name("Imposter").email("imposter@example.com").role(UserRole.STUDENT).build());

      assertThatThrownBy(() ->
              invitationService.acceptInvitation(new AcceptInviteRequest(invitation.token(), imposterStudent.getId()))
      ).isInstanceOf(ForbiddenException.class).hasMessageContaining("not for you");
    }

    @Test
    @DisplayName("should throw exception when invitation is already accepted")
    void throwsException_whenInvitationIsAlreadyAccepted() {
      InvitationResponse invitation = invitationService.createInvitation(new InviteRequest(mentor.getId(), "alex@example.com"));
      invitationService.acceptInvitation(new AcceptInviteRequest(invitation.token(), student.getId()));

      assertThatThrownBy(() ->
              invitationService.acceptInvitation(new AcceptInviteRequest(invitation.token(), student.getId()))
      ).isInstanceOf(ConflictException.class).hasMessageContaining("Invitation is no longer pending");
    }

    @Test
    @DisplayName("should throw exception when student already has a mentor")
    void throwsException_whenStudentAlreadyHasAMentor() {
      // Arrange: Student accepts an invitation from the first mentor
      invitationService.acceptInvitation(
              new AcceptInviteRequest(
                      invitationService.createInvitation(new InviteRequest(mentor.getId(), student.getEmail())).token(),
                      student.getId()
              )
      );

      // Arrange: A second mentor invites the same student
      User secondMentor = userRepository.save(User.builder()
              .id(UUID.randomUUID()).name("Second Mentor").email("second@example.com").role(UserRole.MENTOR).build());
      InvitationResponse secondInvite = invitationService.createInvitation(new InviteRequest(secondMentor.getId(), student.getEmail()));

      // Act & Assert: Student tries to accept the second invite
      assertThatThrownBy(() ->
              invitationService.acceptInvitation(new AcceptInviteRequest(secondInvite.token(), student.getId()))
      ).isInstanceOf(ConflictException.class).hasMessageContaining("Student already has a mentor");
    }
  }
}