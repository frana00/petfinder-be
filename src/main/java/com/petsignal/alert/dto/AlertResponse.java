package com.petsignal.alert.dto;

import com.petsignal.alert.entity.AlertStatus;
import com.petsignal.alert.entity.AlertType;
import com.petsignal.photos.dto.PhotoUrl;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "Alert response data")
public class AlertResponse {

    private Long id;

    private Long userId;

    private AlertType type;

    private AlertStatus status;

    private String chipNumber;

    private String postalCode;

    private String countryCode;

    private LocalDateTime date;

    private String title;

    private String description;

    private String breed;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<PhotoUrl> photoUrls;
} 