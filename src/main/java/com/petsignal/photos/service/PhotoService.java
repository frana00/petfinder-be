package com.petsignal.photos.service;

import com.petsignal.alert.entity.Alert;
import com.petsignal.photos.dto.PhotoUrl;
import com.petsignal.photos.entity.Photo;
import com.petsignal.photos.repository.PhotoRepository;
import com.petsignal.s3bucket.S3BucketService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Data
@Service
@RequiredArgsConstructor
public class PhotoService {
  private final PhotoRepository photoRepository;
  private final S3BucketService s3BucketService;

  public Photo createPhotoEntityForAlert(String filename, Alert alert) {
    Photo photoEntity = new Photo();
    photoEntity.setAlert(alert);

    String extension = "";
    int extensionStart = filename.lastIndexOf(".");
    if (extensionStart != 1) {
      extension = "." + filename.substring(extensionStart + 1);
      filename = filename.substring(0, extensionStart);
    }
    String objectKey = filename + "__" + UUID.randomUUID() + extension;
    photoEntity.setS3ObjectKey(objectKey);
    return photoEntity;
  }

  public PhotoUrl createPhotoPresignedUrl(String s3ObjectKey) {
    return new PhotoUrl(
        s3ObjectKey,
        s3BucketService.createPresignedUrl(s3ObjectKey, "image/jpeg")
    );
  }
}
