package com.mentorship.controller;

import com.mentorship.dto.UserCreateRequest; // 1. Import new DTO
import com.mentorship.dto.UserResponse;
import com.mentorship.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; // 2. Import Valid
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; // 3. Import PostMapping
import org.springframework.web.bind.annotation.RequestBody; // 4. Import RequestBody
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI; // 5. Import URI
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Discover and create mentors and students")
public class UserController {

  private final UserService userService;

  // --- NEW POST METHOD ---
  @PostMapping
  @Operation(
          summary = "Create a new user",
          description = "Creates a new mentor or student. Note: In production, this is normally handled by an auth sign-up flow."
  )
  public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
    // 6. Delegate creation to the service
    UserResponse createdUser = userService.createUser(request);

    // 7. Create a "Location" URL for the new resource
    URI location = URI.create(
            String.format("/api/users/%s", createdUser.id())
    );

    // 8. Return a 201 Created response
    return ResponseEntity.created(location).body(createdUser);
  }

  // --- EXISTING GET METHODS ---
  @GetMapping
  @Operation(
          summary = "List users",
          description = "Returns every user available to the mentorship workflow."
  )
  public ResponseEntity<List<UserResponse>> listUsers() {
    return ResponseEntity.ok(userService.listUsers());
  }

  @GetMapping("/{id}")
  @Operation(
          summary = "Fetch user by ID",
          description = "Returns one user when the provided UUID exists."
  )
  public ResponseEntity<UserResponse> getUser(@PathVariable UUID id) {
    return userService.getUser(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
  }
}