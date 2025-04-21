package com.petsignal.alert.dto;

import com.petsignal.alert.entity.AlertSex;
import com.petsignal.alert.entity.AlertStatus;
import com.petsignal.alert.entity.AlertType;
import com.petsignal.photos.dto.PhotoUrl;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AlertResponse {

  private Long id;

  private Long userId;

  private AlertType type;

  private AlertStatus status;

  private String chipNumber;

  private AlertSex sex;

  private LocalDateTime date;

  private String title;

  private String description;

  private String breed;

  private String postalCode;

  private String countryCode;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  private List<PhotoUrl> photoUrls;
}