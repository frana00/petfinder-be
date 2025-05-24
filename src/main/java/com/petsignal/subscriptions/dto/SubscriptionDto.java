package com.petsignal.subscriptions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petsignal.alert.entity.AlertType;
import com.petsignal.notifications.entity.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SubscriptionDto {

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long id;

  @NotBlank(message = "Username is required")
  private String userId;

  private AlertType alertType;

  @NotNull(message = "Notification type is required")
  private NotificationType notificationType;

  @NotBlank(message = "Country code is required")
  private String countryCode;

  private List<String> postCodes;

}
