package com.mentorship.service;

import com.mentorship.domain.Invitation;
import com.mentorship.domain.InvitationStatus;
import com.mentorship.domain.Mentorship;
import com.mentorship.domain.User;
import com.mentorship.domain.UserRole;
import com.mentorship.dto.AcceptInviteRequest;
import com.mentorship.dto.InviteRequest;
import com.mentorship.dto.InvitationResponse;
import com.mentorship.dto.MentorshipResponse;
import com.mentorship.exception.ConflictException;
import com.mentorship.exception.ForbiddenException;
import com.mentorship.exception.ResourceNotFoundException;
import com.mentorship.repository.InvitationRepository;
import com.mentorship.repository.MentorshipRepository;
import com.mentorship.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvitationService {

  private final UserRepository userRepository;
  private final InvitationRepository invitationRepository;
  private final MentorshipRepository mentorshipRepository;

  @Transactional
  public InvitationResponse createInvitation(InviteRequest request) {
    User mentor = userRepository.findById(request.mentorId())
            .orElseThrow(() -> new ResourceNotFoundException("Mentor not found"));

    if (mentor.getRole() != UserRole.MENTOR) {
      throw new IllegalArgumentException("Only mentors can send invitations");
    }

    String trimmedEmail = request.studentEmail().trim();
    String normalizedEmail = trimmedEmail.toLowerCase(Locale.ROOT);

    if (mentor.getEmail().equalsIgnoreCase(normalizedEmail)) {
      throw new IllegalArgumentException("Mentors cannot invite themselves");
    }

    // Check for existing pending invite
    invitationRepository.findByMentor_IdAndStudentEmailIgnoreCaseAndStatus(
                    mentor.getId(), normalizedEmail, InvitationStatus.PENDING)
            .ifPresent(invitation -> {
              throw new ConflictException("You already have a pending invite for that email");
            });

    String uniqueToken = UUID.randomUUID().toString(); // Simple token

    Invitation newInvitation = Invitation.builder()
            .mentor(mentor)
            .studentEmail(normalizedEmail)
            .status(InvitationStatus.PENDING)
            .token(uniqueToken)
            .build();

    Invitation savedInvitation = invitationRepository.save(newInvitation);
    return InvitationResponse.from(savedInvitation);
  }

  @Transactional
  public MentorshipResponse acceptInvitation(AcceptInviteRequest request) {
    Invitation invitation = invitationRepository.findAndLockByToken(request.token())
            .orElseThrow(() -> new ResourceNotFoundException("Invitation token not found"));

    if (invitation.getStatus() != InvitationStatus.PENDING) {
      throw new ConflictException("Invitation is no longer pending");
    }

    User student = userRepository.findAndLockById(request.studentId())
            .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

    if (student.getRole() != UserRole.STUDENT) {
      throw new IllegalArgumentException("Only students can accept invitations");
    }

    if (!invitation.getStudentEmail().equalsIgnoreCase(student.getEmail())) {
      throw new ForbiddenException("This invitation is not for you.");
    }

    // Check if student already has a mentor
    mentorshipRepository.findByStudent_Id(student.getId())
            .ifPresent(existing -> {
              throw new ConflictException("Student already has a mentor");
            });

    Mentorship newMentorship = Mentorship.builder()
            .mentor(invitation.getMentor())
            .student(student)
            .build();

    Mentorship savedMentorship;
    try {
      savedMentorship = mentorshipRepository.save(newMentorship);
    } catch (DataIntegrityViolationException ex) {
      throw new ConflictException("Student already has a mentor", ex);
    }

    invitation.setStatus(InvitationStatus.ACCEPTED);
    invitationRepository.save(invitation);

    return MentorshipResponse.from(savedMentorship);
  }

  @Transactional(readOnly = true)
  public List<InvitationResponse> getInvitationsForMentor(UUID mentorId) {
    return invitationRepository.findByMentor_Id(mentorId)
            .stream()
            .map(InvitationResponse::from)
            .toList();
  }

  @Transactional(readOnly = true)
  public Optional<InvitationResponse> getInvitationByToken(String token) {
    return invitationRepository.findByToken(token).map(InvitationResponse::from);
  }

  @Transactional(readOnly = true)
  public List<MentorshipResponse> getMentorshipsForMentor(UUID mentorId) {
    return mentorshipRepository.findByMentor_Id(mentorId)
            .stream()
            .map(MentorshipResponse::from)
            .toList();
  }

  @Transactional(readOnly = true)
  public Optional<MentorshipResponse> getMentorshipForStudent(UUID studentId) {
    return mentorshipRepository.findByStudent_Id(studentId).map(MentorshipResponse::from);
  }
}
