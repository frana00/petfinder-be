package com.petsignal.photos.controller;

import com.petsignal.photos.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.MediaType.IMAGE_PNG;

@RestController
@RequestMapping("/photos/s3")
@RequiredArgsConstructor
public class PhotoController {
  private final PhotoService photoService;

  @GetMapping
  public ResponseEntity<byte[]> retrievePhoto(@RequestParam String presignedUrl) {
    return ResponseEntity.ok()
        .header(CONTENT_DISPOSITION, "attachment; filename=photo.jpg")
        .contentType(IMAGE_PNG)
        .body(photoService.retrievePhotoFromS3(presignedUrl));
  }

  @PutMapping
  public String uploadPhoto(@RequestParam MultipartFile file, @RequestParam String presignedUrl,
                            @RequestParam String objectKey) {
    return photoService.uploadPhotoToS3(file, presignedUrl, objectKey);
  }

}
