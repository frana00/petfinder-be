package com.petsignal.user.entity;

import com.petsignal.alert.entity.Alert;
import com.petsignal.posts.entity.Post;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Alert> alerts = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Post> posts = new ArrayList<>();

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  public enum Role {
    ADMIN, USER
  }
} 