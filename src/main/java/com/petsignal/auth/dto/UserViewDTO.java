package com.petsignal.auth.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserViewDTO {
    private Integer id;
    private String username;
    private String email;
    private String subscriptionEmail;
    private String phoneNumber;
    private String role;
    private String createdAt; // Consider using Instant or LocalDateTime if you parse/format it
}
