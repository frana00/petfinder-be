package com.petsignal.alert.service;

import com.petsignal.alert.dto.AlertRequest;
import com.petsignal.alert.dto.AlertResponse;
import com.petsignal.alert.entity.Alert;
import com.petsignal.alert.entity.AlertStatus;
import com.petsignal.alert.entity.AlertType;
import com.petsignal.alert.events.AlertEvent;
import com.petsignal.alert.mapper.AlertMapper;
import com.petsignal.alert.mapper.AlertResponseBuilder;
import com.petsignal.alert.repository.AlertRepository;
import com.petsignal.exception.ResourceNotFoundException;
import com.petsignal.photos.entity.Photo;
import com.petsignal.photos.service.PhotoService;
import com.petsignal.postcodes.entity.PostCode;
import com.petsignal.postcodes.service.PostCodeService;
import com.petsignal.user.entity.User;
import com.petsignal.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.petsignal.alert.entity.AlertType.LOST;
import static com.petsignal.alert.entity.AlertType.SEEN;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;


@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {
  public static final String ALERT = "Alert";
  private final AlertRepository alertRepository;
  private final UserService userService;
  private final AlertMapper alertMapper;
  private final PostCodeService postCodeService;
  private final PhotoService photoService;
  private final AlertResponseBuilder alertBuilder;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional(readOnly = true)
  public Page<AlertResponse> getAllAlerts(
      AlertType type, AlertStatus status, String countryCode, String postalCode, Pageable pageable) {

    return alertRepository.findFilteredAlerts(type, status, countryCode, postalCode, pageable)
        .map(alert -> alertBuilder.build(alert, GET));
  }

  @Transactional(readOnly = true)
  public AlertResponse findAlertById(Long id) {
    Alert alert = alertRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(ALERT, "id", id));
    return alertBuilder.build(alert, GET);
  }

  @Transactional(readOnly = true)
  public Alert findAlertEntityById(Long id) {
    return alertRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(ALERT, "id", id));
  }

  @Transactional
  public AlertResponse createAlert(AlertRequest request) {
    User user = userService.findByUsername(request.getUsername());

    PostCode postCode = postCodeService.findByPostCodeAndCountry(request.getPostalCode(), request.getCountryCode());

    Alert alert = buildAlertAndPhotosEntities(request, postCode, user);

    Alert savedAlert = alertRepository.save(alert);

    eventPublisher.publishEvent(new AlertEvent(savedAlert, AlertEvent.Type.CREATED));

    return alertBuilder.build(savedAlert, PUT);
  }

  @Transactional
  public AlertResponse updateAlert(Long id, AlertRequest request) {
    Alert alert = alertRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(ALERT, "id", id));

    alertMapper.updateEntityFromRequest(request, alert);
    // find postcode in database before saving
    PostCode postCode = postCodeService
        .findByPostCodeAndCountry(request.getPostalCode(), request.getCountryCode());
    alert.setPostCode(postCode);
    Alert updatedAlert = alertRepository.save(alert);

    AlertEvent.Type reason = getNotificationReason(request, alert);
    eventPublisher.publishEvent(new AlertEvent(updatedAlert, reason));

    return alertBuilder.build(updatedAlert, GET);
  }

  private static AlertEvent.Type getNotificationReason(AlertRequest request, Alert alert) {
    if (AlertStatus.RESOLVED.equals(request.getStatus()) && AlertStatus.RESOLVED.equals(alert.getStatus())) {
      return AlertEvent.Type.RESOLVED;
    } else {
      return AlertEvent.Type.UPDATED;
    }
  }

  @Transactional
  public void deleteAlert(Long id) {
    Alert alert = alertRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ALERT, "id", id));

    // delete alert from DB
    alert.setDeleted(true);

    eventPublisher.publishEvent(new AlertEvent(alert, AlertEvent.Type.DELETED));

    // delete photos from s3
    photoService.deletePhotos(alert.getPhotos());

  }

  @Transactional
  public List<Alert> getLatestResolvedAlerts(LocalDateTime from, LocalDateTime to) {
    return alertRepository.findWithPhotosByStatusAndUpdatedAtBetween(AlertStatus.RESOLVED, from, to);
  }

  private Alert buildAlertAndPhotosEntities(AlertRequest request, PostCode postCode, User user) {
    Alert alert = alertMapper.toEntity(request);
    alert.setPostCode(postCode);
    alert.setUser(user);

    List<String> photoFilenames = request.getPhotoFilenames();
    if (photoFilenames != null && !photoFilenames.isEmpty()) {
      List<Photo> photos = photoFilenames.stream()
          .map(filename -> photoService.createPhotoEntityForAlert(filename, alert))
          .toList();

      alert.setPhotos(photos);
    }
    return alert;
  }

  public List<Alert> getOppositeAlertsInPostcode(Alert alert) {
    AlertType type = SEEN.equals(alert.getType()) ? LOST : SEEN;

    return alertRepository.findByTypeAndPostCodeAndDeletedFalse(type, alert.getPostCode());
  }
}