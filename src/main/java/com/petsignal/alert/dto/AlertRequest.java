package com.petsignal.alert.dto;

import com.petsignal.alert.entity.AlertSex;
import com.petsignal.alert.entity.AlertStatus;
import com.petsignal.alert.entity.AlertType;
import com.petsignal.alert.validator.ValidFileExtension;
import com.petsignal.alert.validator.ValidGpsCoordinates;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ValidGpsCoordinates
public class AlertRequest {

  @NotBlank(message = "Username is required")
  private String username;

  @NotNull(message = "Alert type is required")
  private AlertType type;

  @NotNull(message = "Alert status is required")
  private AlertStatus status;

  private String chipNumber;

  private AlertSex sex;

  @NotNull(message = "Date is required")
  private LocalDateTime date;

  @NotBlank(message = "Title is required")
  @Size(max = 100, message = "Title must be at most 100 characters")
  private String title;

  @NotBlank(message = "Description is required")
  @Size(max = 300, message = "Description must be at most 300 characters")
  private String description;

  @Size(max = 100, message = "Breed must be at most 100 characters")
  private String breed;

  @Size(max = 20, message = "Postal code cannot exceed 20 characters")
  private String postalCode;

  @Size(max = 3, message = "Country code cannot exceed 3 characters")
  private String countryCode;

  private String location;

  private Double latitude;

  private Double longitude;

  private String locationSource;

  @Size(max = 5, message = "You can upload a maximum of 5 photos")
  @ValidFileExtension
  private List<String> photoFilenames;
} 