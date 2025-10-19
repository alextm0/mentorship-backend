package com.mentorship.repository;

import com.mentorship.domain.Mentorship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MentorshipRepository extends JpaRepository<Mentorship, UUID> {
  List<Mentorship> findByMentor_Id(UUID mentorId);
  Optional<Mentorship> findByStudent_Id(UUID studentId);
}
