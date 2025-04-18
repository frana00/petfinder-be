package com.petsignal.alert.service;

import com.petsignal.alert.dto.AlertRequest;
import com.petsignal.alert.dto.AlertResponse;
import com.petsignal.alert.entity.Alert;
import com.petsignal.alert.mapper.AlertMapper;
import com.petsignal.alert.repository.AlertRepository;
import com.petsignal.postcodes.entity.PostCode;
import com.petsignal.postcodes.service.PostCodeService;
import com.petsignal.s3bucket.S3BucketService;
import com.petsignal.user.service.UserService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {
    private final AlertRepository alertRepository;
    private final UserService userService;
    private final AlertMapper alertMapper;
    private final PostCodeService postCodeService;
    private final S3BucketService s3BucketService;

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

        // obtain presigned urls
        List<String> presignedPhotoUrls = request.getPhotos().stream().map(photo -> s3BucketService.createPresignedUrl(photo, "image/jpeg")).toList();
        log.info("Photo urls: {}", presignedPhotoUrls);
        return alertMapper.toResponse(alertRepository.save(alert));
    
    }
} 