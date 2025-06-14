package com.petsignal.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

  @Email(message = "Invalid email format")
  @Pattern(regexp = "^([A-Za-z0-9+_.-]+@(.+)\\.[A-Za-z]{2,})?$", message = "Email must contain @ and a valid domain")
  @Size(max = 100, message = "Email must not exceed 100 characters")
  private String email;

  @Email(message = "Invalid subscription email format")
  @Pattern(regexp = "^([A-Za-z0-9+_.-]+@(.+)\\.[A-Za-z]{2,})?$", message = "Subscription email must contain @ and a valid domain")
  @Size(max = 100, message = "Subscription email must not exceed 100 characters")
  private String subscriptionEmail;

  @Size(max = 20, message = "Phone number must not exceed 20 characters")
  @Pattern(regexp = "^[+]?[0-9\\s\\-()]{0,20}$", message = "Phone number can only contain numbers, spaces, hyphens, parentheses and plus sign")
  private String phoneNumber;
}
