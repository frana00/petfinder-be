package com.petsignal.user.controller;

import com.petsignal.user.dto.CreateUserRequest;
import com.petsignal.user.dto.UpdateUserRequest;
import com.petsignal.user.dto.UserResponse;
import com.petsignal.user.entity.User; // Import User entity
import com.petsignal.user.mapper.UserMapper; // Import UserMapper
import com.petsignal.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;
  private final UserMapper userMapper; // Inject UserMapper

  @GetMapping
  public List<UserResponse> getUsers() {
    return userService.getAllUsers();
  }

  @GetMapping("/{id}")
  public UserResponse getUser(@PathVariable Long id) {
    return userService.findById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
    return userService.createUser(request);
  }

  @PutMapping("/{id}")
  public UserResponse updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
    return userService.updateUser(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
  }

  @GetMapping("/me")
  public UserResponse getCurrentUser(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      // This case should ideally be handled by Spring Security's authorization
      // but as a safeguard or for specific error handling:
      throw new IllegalStateException("User not authenticated");
    }
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    User user = userService.findByUsername(userDetails.getUsername());
    return userMapper.toResponse(user); 
  }
} 