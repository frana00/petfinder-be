package com.petsignal.auth.entity;

import com.petsignal.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "password_reset_tokens",
       indexes = {
           @Index(name = "idx_token_hash", columnList = "tokenHash"),
           @Index(name = "idx_email_created", columnList = "email, createdAt")
       })
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_hash", unique = true, nullable = false)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public PasswordResetToken(String tokenHash, User user, String email, LocalDateTime expiresAt, String ipAddress) {
        this.tokenHash = tokenHash;
        this.user = user;
        this.email = email;
        this.expiresAt = expiresAt;
        this.ipAddress = ipAddress;
    }
}
