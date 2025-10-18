package com.mentorship.repository;

import com.mentorship.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  boolean existsByEmailIgnoreCase(@NotBlank(message = "Email cannot be blank") @Email(message = "Must be a valid email format") String email);
}
