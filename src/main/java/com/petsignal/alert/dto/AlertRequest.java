package com.petsignal.alert.dto;

import com.petsignal.alert.entity.AlertStatus;
import com.petsignal.alert.entity.AlertType;
import com.petsignal.alert.validator.ValidFileExtension;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "Request to create or update an alert")
public class AlertRequest {

  @NotNull(message = "User ID is required")
  private Long userId;

  @NotNull(message = "Alert type is required")
  private AlertType type;

  @NotNull(message = "Alert status is required")
  private AlertStatus status;

  private String chipNumber;

  @NotBlank(message = "Postal code is required")
  @Pattern(regexp = "^\\d{5}$", message = "Postal code must be 5 digits")
  private String postalCode;

  @NotNull(message = "Country code is required")
  private String countryCode;

  @NotNull(message = "Date is required")
  private LocalDateTime date;

  @Size(max = 100)
  private String title;

  private String description;

  @Size(max = 100)
  private String breed;

  @ValidFileExtension
  private List<String> photoFilenames;
} 