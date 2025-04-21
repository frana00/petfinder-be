package com.petsignal.alert.service;

import com.petsignal.alert.dto.AlertRequest;
import com.petsignal.alert.dto.AlertResponse;
import com.petsignal.alert.entity.Alert;
import com.petsignal.alert.mapper.AlertMapper;
import com.petsignal.alert.repository.AlertRepository;
import com.petsignal.exception.ResourceNotFoundException;
import com.petsignal.photos.dto.PhotoUrl;
import com.petsignal.photos.entity.Photo;
import com.petsignal.photos.service.PhotoService;
import com.petsignal.postcodes.entity.PostCode;
import com.petsignal.postcodes.service.PostCodeService;
import com.petsignal.user.service.UserService;
import io.swagger.models.HttpMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static io.swagger.models.HttpMethod.GET;
import static io.swagger.models.HttpMethod.PUT;

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

  @Transactional(readOnly = true)
  public List<AlertResponse> getAllAlerts() {
    return alertRepository.findAll().stream()
        .map(alert -> buildAlertResponseWithPresignedUrls(alert, GET))
        .toList();
  }

  @Transactional(readOnly = true)
  public AlertResponse getAlertById(Long id) {
    Alert alert = alertRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(ALERT, "id", id));
    return buildAlertResponseWithPresignedUrls(alert, GET);
  }

  @Transactional
  public AlertResponse createAlert(AlertRequest request) {
    // Validate user exists
    userService.findById(request.getUserId());

    PostCode postCode = postCodeService.findByPostCodeAndCountry(request.getPostalCode(), request.getCountryCode());

    Alert alert = buildAlertAndPhotosEntities(request, postCode);

    Alert savedAlert = alertRepository.save(alert);

    return buildAlertResponseWithPresignedUrls(savedAlert, PUT);
  }

  @Transactional
  public AlertResponse updateAlert(Long id, AlertRequest request) {
    Alert alert = alertRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(ALERT, "id", id));

    alertMapper.updateEntityFromRequest(request, alert);
    Alert updatedAlert = alertRepository.save(alert);
    return buildAlertResponseWithPresignedUrls(updatedAlert, GET);
  }

  @Transactional
  public void deleteAlert(Long id) {
    if (!alertRepository.existsById(id)) {
      throw new ResourceNotFoundException(ALERT, "id", id);
    }
    alertRepository.deleteById(id);
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

  private AlertResponse buildAlertResponseWithPresignedUrls(Alert alert, HttpMethod method) {
    AlertResponse response = alertMapper.toResponse(alert);

    List<PhotoUrl> presignedUrls = alert.getPhotos().stream()
        .map(photo -> photoService.createPhotoPresignedUrl(photo.getS3ObjectKey(), method))
        .toList();

    response.setPhotoUrls(presignedUrls);
    return response;
  }
} 