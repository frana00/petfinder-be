package com.petsignal.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(name = "User")
public class UserResponseDto {
    private Integer id;
    private String username;
    private String email;
    private String subscriptionEmail;
    private String phoneNumber;
    private String role;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
} 