package com.petsignal.alert.controller;

import com.petsignal.alert.dto.AlertRequest;
import com.petsignal.alert.dto.AlertResponse;
import com.petsignal.alert.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
@Tag(name = "Alerts", description = "Alert management APIs")
public class AlertController {
    private final AlertService alertService;

    @Operation(summary = "Create a new alert")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlertResponse createAlert(@Valid @RequestBody AlertRequest request) {
        return (alertService.createAlert(request));
    }
} 