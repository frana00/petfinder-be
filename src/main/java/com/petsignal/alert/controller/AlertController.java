package com.petsignal.alert.controller;

import com.petsignal.alert.dto.AddPhotosToAlertRequest;
import com.petsignal.alert.dto.AlertRequest;
import com.petsignal.alert.dto.AlertResponse;
import com.petsignal.alert.entity.AlertStatus;
import com.petsignal.alert.entity.AlertType;
import com.petsignal.alert.service.AlertPhotoService;
import com.petsignal.alert.service.AlertService;
import com.petsignal.photos.dto.PhotoUrl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class AlertController {

  private final AlertService alertService;
  private final AlertPhotoService alertPhotoService;

  @GetMapping
  public ResponseEntity<Page<AlertResponse>> getAllAlerts(
      @RequestParam(required = false) AlertType type,
      @RequestParam(required = false) AlertStatus status,
      @RequestParam(required = false) String countryCode,
      @RequestParam(required = false) String postalCode,
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
  ) {
    Page<AlertResponse> alerts = alertService.getAllAlerts(type, status, countryCode, postalCode, pageable);
    return ResponseEntity.ok(alerts);
  }

  @GetMapping("/{id}")
  public ResponseEntity<AlertResponse> getAlertById(@PathVariable Long id) {
    AlertResponse alert = alertService.findAlertById(id);
    return ResponseEntity.ok(alert);
  }

  @PostMapping
  public ResponseEntity<AlertResponse> createAlert(@Valid @RequestBody AlertRequest request) {
    AlertResponse createdAlert = alertService.createAlert(request);
    return new ResponseEntity<>(createdAlert, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<AlertResponse> updateAlert(@PathVariable Long id, @Valid @RequestBody AlertRequest request) {
    AlertResponse updatedAlert = alertService.updateAlert(id, request);
    return ResponseEntity.ok(updatedAlert);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAlert(@PathVariable Long id) {
    alertService.deleteAlert(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{id}/photos")
  public ResponseEntity<List<PhotoUrl>> addPhotosToAlert(@PathVariable Long id,
                                                         @Valid @RequestBody AddPhotosToAlertRequest request) {
    return ResponseEntity.ok(alertPhotoService.addPhotosToAlert(id, request.getPhotoFilenames()));
  }


  @DeleteMapping("/{alertId}/photos/{s3ObjectKey:.+}")
  public ResponseEntity<Void> deletePhotoFromAlert(@PathVariable Long alertId, @PathVariable String s3ObjectKey) {
    alertPhotoService.deletePhotoFromAlert(alertId, s3ObjectKey);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/nearby")
  public ResponseEntity<List<AlertResponse>> getAlertsNearby(
      @RequestParam Double lat,
      @RequestParam Double lng,
      @RequestParam(defaultValue = "10.0") Double radius,
      @RequestParam(required = false) AlertType type
  ) {
    List<AlertResponse> nearbyAlerts = alertService.findAlertsWithinRadius(lat, lng, radius, type);
    return ResponseEntity.ok(nearbyAlerts);
  }
} 