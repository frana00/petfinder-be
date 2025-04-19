package com.petsignal.photos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Schema(description = "Photo url data")
@AllArgsConstructor
public class PhotoUrl {

  private String s3ObjectKey;

  private String presignedUrl;

}
