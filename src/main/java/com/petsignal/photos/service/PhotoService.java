package com.petsignal.photos.service;

import com.petsignal.alert.entity.Alert;
import com.petsignal.photos.dto.PhotoUrl;
import com.petsignal.photos.entity.Photo;
import com.petsignal.photos.repository.PhotoRepository;
import com.petsignal.s3bucket.S3BucketService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.springframework.http.MediaType.IMAGE_PNG;

@Data
@Slf4j
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
        s3BucketService.createPutPresignedUrl(s3ObjectKey, IMAGE_PNG)
    );
  }

  public String uploadPhotoToS3(MultipartFile file, String presignedUrl, String s3ObjectKey) {
    File tempFile = null;
    try {
      tempFile = File.createTempFile("upload-", "-" + file.getOriginalFilename());
      file.transferTo(tempFile);
      s3BucketService.uploadFileWithPresignedUrl(presignedUrl, tempFile, IMAGE_PNG);
      return s3BucketService.createGetPresignedUrl(s3ObjectKey, IMAGE_PNG);
    } catch (IOException e) {
      log.warn("Could not read file {}", file.getOriginalFilename(), e);
      return null;
    } finally {
      if (tempFile != null) {
        try {
          Files.delete(Path.of(tempFile.getAbsolutePath()));
        } catch (IOException e) {
          log.warn("Temporary auxiliary file could not be deleted");
        }
      }
    }
  }

  public byte[] retrievePhotoFromS3(String presignedUrl) {
    return s3BucketService.useSdkHttpClientToGet(presignedUrl, IMAGE_PNG);
  }
}
