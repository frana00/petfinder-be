package com.petsignal.alert.service;

import com.petsignal.alert.dto.AlertRequest;
import com.petsignal.alert.dto.AlertResponse;
import com.petsignal.alert.entity.Alert;
import com.petsignal.alert.mapper.AlertMapper;
import com.petsignal.alert.repository.AlertRepository;
import com.petsignal.photos.dto.PhotoUrl;
import com.petsignal.photos.entity.Photo;
import com.petsignal.photos.service.PhotoService;
import com.petsignal.postcodes.entity.PostCode;
import com.petsignal.postcodes.service.PostCodeService;
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
  private final PhotoService photoService;

  @Transactional
  public AlertResponse createAlert(AlertRequest request) {
    Long userId = request.getUserId();
    // Confirm user exists
    if (userService.findById(userId) == null) {
      throw new EntityNotFoundException("User not found with id: " + userId);
    }

    PostCode postCode = postCodeService.findByPostCodeAndCountry(request.getPostalCode(), request.getCountryCode());

    Alert alert = buildAlertAndPhotosEntities(request, postCode);

    Alert savedAlert = alertRepository.save(alert);

    return buildAlertResponseWithPresignedUrls(savedAlert);

  }

  private Alert buildAlertAndPhotosEntities(AlertRequest request, PostCode postCode) {
    Alert alert = alertMapper.toEntity(request);
    alert.setPostCode(postCode);

    List<Photo> photos = request.getPhotoFilenames().stream()
        .map(filename -> photoService.createPhotoEntityForAlert(filename, alert))
        .toList();

    alert.setPhotos(photos);
    return alert;
  }


  private AlertResponse buildAlertResponseWithPresignedUrls(Alert alert) {
    AlertResponse response = alertMapper.toResponse(alert);

    List<PhotoUrl> presignedUrls = alert.getPhotos().stream()
        .map(photo -> photoService.createPhotoPresignedUrl(photo.getS3ObjectKey()))
        .toList();

    response.setPhotoUrls(presignedUrls);
    return response;
  }

} 