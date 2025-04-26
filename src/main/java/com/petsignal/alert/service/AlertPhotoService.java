package com.petsignal.alert.service;

import com.petsignal.alert.entity.Alert;
import com.petsignal.exception.BadRequestException;
import com.petsignal.photos.dto.PhotoUrl;
import com.petsignal.photos.entity.Photo;
import com.petsignal.photos.repository.PhotoRepository;
import com.petsignal.photos.service.PhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpMethod.PUT;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertPhotoService {

  private final AlertService alertService;
  private final PhotoService photoService;
  private final PhotoRepository photoRepository;


  public List<PhotoUrl> addPhotosToAlert(Long alertId, List<String> photoFilenames) {
    Alert alert = alertService.findAlertEntityById(alertId);

    // Confirm maximum number of photos per alert not exceeded
    if (alert.getPhotos().size() + photoFilenames.size() > 5) {
      throw new BadRequestException("Adding these photos would exceed the maximum limit of 5 photos per alert");
    }

    List<Photo> newPhotos = photoFilenames.stream()
        .map(filename -> photoService.createPhotoEntityForAlert(filename, alert))
        .toList();

    photoRepository.saveAll(newPhotos);

    return newPhotos.stream()
        .map(photo -> photoService.createPhotoPresignedUrl(photo.getS3ObjectKey(), PUT))
        .toList();
  }

  @Transactional
  public void deletePhotoFromAlert(Long alertId, String s3ObjectKey) {
    Alert alert = alertService.findAlertEntityById(alertId);

    Photo photo = alert.getPhotos().stream()
        .filter(p -> Objects.equals(p.getS3ObjectKey(), s3ObjectKey))
        .findFirst()
        .orElseThrow(() -> new BadRequestException(String.format("Alert %d does not contain photo with s3ObjectKey %s", alertId, s3ObjectKey)));

    photoService.deletePhotos(List.of(photo));

    alert.getPhotos().remove(photo);

  }
}
