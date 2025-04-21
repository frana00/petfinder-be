package com.petsignal.user.controller;

import com.petsignal.user.dto.UserRequest;
import com.petsignal.user.dto.UserResponse;
import com.petsignal.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

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
  public UserResponse createUser(@Valid @RequestBody UserRequest request) {
    return userService.createUser(request);
  }

  @PutMapping("/{id}")
  public UserResponse updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
    return userService.updateUser(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
  }
} 