package com.mentorship.service;

import com.mentorship.domain.User;
import com.mentorship.dto.UserCreateRequest;
import com.mentorship.dto.UserResponse;
import com.mentorship.exception.ConflictException;
import com.mentorship.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  @Transactional(readOnly = true)
  public List<UserResponse> listUsers() {
    return userRepository.findAll()
            .stream()
            .map(UserResponse::from)
            .toList();
  }

  @Transactional(readOnly = true)
  public Optional<UserResponse> getUser(UUID id) {
    return userRepository.findById(id).map(UserResponse::from);
  }

  @Transactional
  public UserResponse createUser(UserCreateRequest request) {
    // Check for conflicts
    if (userRepository.existsById(request.id())) {
      throw new ConflictException("User with ID " + request.id() + " already exists");
    }
    if (userRepository.existsByEmailIgnoreCase(request.email())) {
      throw new ConflictException("User with email " + request.email() + " already exists");
    }

    // Create new user entity
    User newUser = User.builder()
            .id(request.id())
            .name(request.name())
            .email(request.email().toLowerCase(Locale.ROOT)) // Normalize email
            .role(request.role())
            .build();

    User savedUser = userRepository.save(newUser);

    // Convert to response DTO
    return UserResponse.from(savedUser);
  }
}
