package com.petsignal.photos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PhotoUrl {

  private String s3ObjectKey;

  private String presignedUrl;

}
