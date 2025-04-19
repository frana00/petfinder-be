package com.petsignal.photos.entity;

import com.petsignal.alert.entity.Alert;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "photos")
public class Photo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "alert_id", nullable = false)
  private Alert alert;

  @Column(name = "s3_object_key", nullable = false)
  private String s3ObjectKey;
}
