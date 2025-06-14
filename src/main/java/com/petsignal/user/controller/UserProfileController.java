package com.petsignal.user.controller;

import com.petsignal.user.dto.UpdateProfileRequest;
import com.petsignal.user.dto.UserResponse;
import com.petsignal.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "Operations for user profile management")
public class UserProfileController {
  
  private final UserService userService;

  @Operation(summary = "Update user profile", 
             description = "Allows authenticated users to update their own profile information. Admins can update any user profile.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid input data or email already exists"),
      @ApiResponse(responseCode = "401", description = "Invalid credentials"),
      @ApiResponse(responseCode = "403", description = "You can only update your own profile"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  @PatchMapping("/{userId}")
  public ResponseEntity<UserResponse> updateProfile(
      @PathVariable Long userId,
      @Valid @RequestBody UpdateProfileRequest request) {
    
    UserResponse updatedUser = userService.updateProfile(userId, request);
    return ResponseEntity.ok(updatedUser);
  }
}
