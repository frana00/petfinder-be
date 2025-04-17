package com.petsignal.user.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50)
    private String username;
    
    @Column(nullable = false, length = 100, unique = true)
    private String email;
    
    @Column(name = "subscription_email", nullable = false, length = 100)
    private String subscriptionEmail;
    
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public enum Role {
        ADMIN, USER
    }
} 