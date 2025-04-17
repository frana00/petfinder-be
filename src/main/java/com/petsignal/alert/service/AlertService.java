package com.petsignal.alert.service;

import com.petsignal.alert.dto.AlertRequest;
import com.petsignal.alert.dto.AlertResponse;
import com.petsignal.alert.entity.Alert;
import com.petsignal.alert.mapper.AlertMapper;
import com.petsignal.alert.repository.AlertRepository;
import com.petsignal.postcodes.entity.PostCode;
import com.petsignal.postcodes.service.PostCodeService;
import com.petsignal.user.service.UserService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlertService {
    private final AlertRepository alertRepository;
    private final UserService userService;
    private final AlertMapper alertMapper;
    private final PostCodeService postCodeService;

    @Transactional
    public AlertResponse createAlert(AlertRequest request) {
        Long userId = request.getUserId();
        // Confirm user exists
        if (userService.findById(userId) == null) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }

        // Obtain postal code from postal code service
        PostCode postCode = postCodeService.findByPostCodeAndCountry(request.getPostalCode(), request.getCountryCode());

        Alert alert = alertMapper.toEntity(request);
        alert.setPostCode(postCode);
        return alertMapper.toResponse(alertRepository.save(alert));
    
    }
} 