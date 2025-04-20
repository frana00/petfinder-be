package com.petsignal.alert.controller;

import com.petsignal.alert.dto.AlertRequest;
import com.petsignal.alert.dto.AlertResponse;
import com.petsignal.alert.service.AlertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class AlertController {
  private final AlertService alertService;

  @PostMapping
  @ResponseStatus(CREATED)
  public AlertResponse createAlert(@Valid @RequestBody AlertRequest request) {
    return (alertService.createAlert(request));
  }

} 