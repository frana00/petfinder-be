package com.petsignal.alert.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.petsignal.postcodes.entity.PostCode;
import com.petsignal.user.entity.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "alerts")
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertStatus status;

    @Column(name = "chip_number")
    private String chipNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "sex")
    private AlertSex sex;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String breed;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postal_code_id", nullable = false)
    private PostCode postCode;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
} 