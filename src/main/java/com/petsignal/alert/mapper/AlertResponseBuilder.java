package com.petsignal.alert.mapper;

import com.petsignal.alert.dto.AlertResponse;
import com.petsignal.alert.entity.Alert;
import com.petsignal.photos.dto.PhotoUrl;
import com.petsignal.photos.service.PhotoService;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AlertResponseBuilder {
  private final AlertMapper alertMapper;
  private final PhotoService photoService;

  public AlertResponseBuilder(AlertMapper alertMapper, PhotoService photoService) {
    this.alertMapper = alertMapper;
    this.photoService = photoService;
  }

  public AlertResponse build(Alert alert, HttpMethod method) {
    AlertResponse response = alertMapper.toResponse(alert);
    List<PhotoUrl> presignedUrls = alert.getPhotos().stream()
        .map(photo -> photoService.createPhotoPresignedUrl(photo.getS3ObjectKey(), method))
        .toList();
    response.setPhotoUrls(presignedUrls);
    return response;
  }
}
