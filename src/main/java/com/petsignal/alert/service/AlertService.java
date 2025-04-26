package com.petsignal.alert.service;

import com.petsignal.alert.dto.AlertRequest;
import com.petsignal.alert.dto.AlertResponse;
import com.petsignal.alert.entity.Alert;
import com.petsignal.alert.entity.AlertStatus;
import com.petsignal.alert.entity.AlertType;
import com.petsignal.alert.mapper.AlertMapper;
import com.petsignal.alert.mapper.AlertResponseBuilder;
import com.petsignal.alert.repository.AlertRepository;
import com.petsignal.exception.ResourceNotFoundException;
import com.petsignal.photos.entity.Photo;
import com.petsignal.photos.service.PhotoService;
import com.petsignal.postcodes.entity.PostCode;
import com.petsignal.postcodes.service.PostCodeService;
import com.petsignal.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;


@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {
  private static final String ALERT = "Alert";
  private final AlertRepository alertRepository;
  private final UserService userService;
  private final AlertMapper alertMapper;
  private final PostCodeService postCodeService;
  private final PhotoService photoService;
  private final AlertResponseBuilder alertBuilder;

  @Transactional(readOnly = true)
  public Page<AlertResponse> getAllAlerts(
      AlertType type, AlertStatus status, String countryCode, String postalCode, Pageable pageable) {

    return alertRepository.findFilteredAlerts(type, status, countryCode, postalCode, pageable)
        .map(alert -> alertBuilder.build(alert, GET));
  }

  @Transactional(readOnly = true)
  public AlertResponse getAlertById(Long id) {
    Alert alert = alertRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(ALERT, "id", id));
    return alertBuilder.build(alert, GET);
  }

  @Transactional
  public AlertResponse createAlert(AlertRequest request) {
    // Validate user exists
    userService.findById(request.getUserId());

    PostCode postCode = postCodeService.findByPostCodeAndCountry(request.getPostalCode(), request.getCountryCode());

    Alert alert = buildAlertAndPhotosEntities(request, postCode);

    Alert savedAlert = alertRepository.save(alert);

    return alertBuilder.build(savedAlert, PUT);
  }

  @Transactional
  public AlertResponse updateAlert(Long id, AlertRequest request) {
    Alert alert = alertRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(ALERT, "id", id));

    alertMapper.updateEntityFromRequest(request, alert);
    Alert updatedAlert = alertRepository.save(alert);
    return alertBuilder.build(updatedAlert, GET);
  }

  @Transactional
  public void deleteAlert(Long id) {
    Alert alert = alertRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ALERT, "id", id));

    // delete alert from DB
    alertRepository.deleteById(id);

    // delete photos from s3
    photoService.deletePhotos(alert.getPhotos());

  }

  @Transactional
  public List<Alert> getLatestResolvedAlerts(LocalDateTime from, LocalDateTime to) {
    return alertRepository.findWithPhotosByStatusAndUpdatedAtBetween(AlertStatus.RESOLVED, from, to);
  }

  private Alert buildAlertAndPhotosEntities(AlertRequest request, PostCode postCode) {
    Alert alert = alertMapper.toEntity(request);
    alert.setPostCode(postCode);

    List<String> photoFilenames = request.getPhotoFilenames();
    if (photoFilenames != null && !photoFilenames.isEmpty()) {
      List<Photo> photos = photoFilenames.stream()
          .map(filename -> photoService.createPhotoEntityForAlert(filename, alert))
          .toList();

      alert.setPhotos(photos);
    }
    return alert;
  }
} 