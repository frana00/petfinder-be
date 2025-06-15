package com.petsignal.alert.entity;

import com.petsignal.photos.entity.Photo;
import com.petsignal.postcodes.entity.PostCode;
import com.petsignal.posts.entity.Post;
import com.petsignal.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "alerts")
public class Alert {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
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
  @Column
  private AlertSex sex;

  @Column(nullable = false)
  private LocalDateTime date;

  @Column(length = 100)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(length = 100)
  private String breed;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "postal_code_id", nullable = true)
  private PostCode postCode;

  @Column(length = 255)
  private String location;

  @Column(name = "latitude")
  private Double latitude;

  @Column(name = "longitude")
  private Double longitude;

  @Column(name = "location_source", length = 10)
  private String locationSource;

  @OneToMany(mappedBy = "alert", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Photo> photos = new ArrayList<>();

  @OneToMany(mappedBy = "alert", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Post> posts = new ArrayList<>();

  @Column(name = "updated_at", nullable = false)
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column
  private boolean deleted;
} 