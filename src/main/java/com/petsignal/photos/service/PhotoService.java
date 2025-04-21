package com.petsignal.photos.service;

import com.petsignal.alert.entity.Alert;
import com.petsignal.exception.UnsupportedFileTypeException;
import com.petsignal.photos.dto.PhotoUrl;
import com.petsignal.photos.entity.Photo;
import com.petsignal.photos.repository.PhotoRepository;
import com.petsignal.s3bucket.S3BucketService;
import io.micrometer.common.util.StringUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.MediaType.IMAGE_JPEG;
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

    int extensionStart = filename.lastIndexOf(".");
    String extension = extractFileExtensionFromName(filename);
    if (StringUtils.isNotBlank(extension)) {
      extension = "." + extension;
      filename = filename.substring(0, extensionStart);
    }
    String objectKey = filename + "__" + UUID.randomUUID() + extension;
    photoEntity.setS3ObjectKey(objectKey);
    return photoEntity;
  }

  public PhotoUrl createPhotoPresignedUrl(String s3ObjectKey, HttpMethod method) {
    if (PUT.equals(method)) {
      return new PhotoUrl(
          s3ObjectKey,
          s3BucketService.createPutPresignedUrl(s3ObjectKey, getMediaType(s3ObjectKey))
      );
    } else {
      return new PhotoUrl(
          s3ObjectKey,
          s3BucketService.createGetPresignedUrl(s3ObjectKey, getMediaType(s3ObjectKey))
      );
    }
  }

  public String uploadPhotoToS3(String s3ObjectKey, String presignedUrl, MultipartFile file) {
    File tempFile = null;
    try {
      tempFile = File.createTempFile("upload-", "-" + file.getOriginalFilename());
      file.transferTo(tempFile);
      s3BucketService.uploadFileWithPresignedUrl(presignedUrl, tempFile, getMediaType(s3ObjectKey));
      return s3BucketService.createGetPresignedUrl(s3ObjectKey, getMediaType(s3ObjectKey));
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

  public byte[] retrievePhotoFromS3(String s3ObjectKey, String presignedUrl) {
    return s3BucketService.retrieveFileFromS3(presignedUrl, getMediaType(s3ObjectKey));
  }

  public MediaType getMediaType(String objectKey) {
    String extension = extractFileExtensionFromName(objectKey);
    return switch (extension) {
      case "png" -> IMAGE_PNG;
      case "jpg", "jpeg" -> IMAGE_JPEG;
      default -> throw new UnsupportedFileTypeException("Unsupported or missing file extension: " + extension);
    };
  }

  private String extractFileExtensionFromName(String filename) {
    String extension = "";
    int extensionStart = filename.lastIndexOf(".");
    if (extensionStart != 1) {
      extension = filename.substring(extensionStart + 1);
    }
    return extension;
  }
}
