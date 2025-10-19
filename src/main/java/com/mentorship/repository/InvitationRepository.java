package com.mentorship.repository;

import com.mentorship.domain.Invitation;
import com.mentorship.domain.InvitationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, UUID> {
  Optional<Invitation> findByToken(String token);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select i from Invitation i where i.token = :token")
  Optional<Invitation> findAndLockByToken(@Param("token") String token);

  List<Invitation> findByMentor_Id(UUID mentorId);
  Optional<Invitation> findByMentor_IdAndStudentEmailIgnoreCaseAndStatus(UUID mentorId, String studentEmail, InvitationStatus status);
}
