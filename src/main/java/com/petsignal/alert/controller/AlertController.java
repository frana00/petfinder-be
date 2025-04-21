package com.petsignal.alert.controller;

import com.petsignal.alert.dto.AlertRequest;
import com.petsignal.alert.dto.AlertResponse;
import com.petsignal.alert.service.AlertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class AlertController {

  private final AlertService alertService;

  @GetMapping
  public ResponseEntity<List<AlertResponse>> getAllAlerts() {
    List<AlertResponse> alerts = alertService.getAllAlerts();
    return ResponseEntity.ok(alerts);
  }

  @GetMapping("/{id}")
  public ResponseEntity<AlertResponse> getAlertById(@PathVariable Long id) {
    AlertResponse alert = alertService.getAlertById(id);
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
} 