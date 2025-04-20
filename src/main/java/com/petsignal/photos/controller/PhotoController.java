package com.petsignal.photos.controller;

import com.petsignal.photos.service.PhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

@RestController
@RequestMapping("/photos/s3")
@RequiredArgsConstructor
@Slf4j
public class PhotoController {
  private final PhotoService photoService;

  @GetMapping(value = "/{s3ObjectKey:.+}")
  public ResponseEntity<byte[]> retrievePhoto(@PathVariable String s3ObjectKey,
                                              @RequestParam String presignedUrl) {

    MediaType mediaType = photoService.getMediaType(s3ObjectKey);
    ContentDisposition contentDisposition = ContentDisposition
        .attachment()
        .filename(s3ObjectKey)
        .build();

    return ResponseEntity.ok()
        .header(CONTENT_DISPOSITION, contentDisposition.toString())
        .contentType(mediaType)
        .body(photoService.retrievePhotoFromS3(s3ObjectKey, presignedUrl));
  }

  @PutMapping
  public String uploadPhoto(@RequestParam MultipartFile file, @RequestParam String presignedUrl,
                            @RequestParam String objectKey) {
    return photoService.uploadPhotoToS3(objectKey, presignedUrl, file);
  }

}
