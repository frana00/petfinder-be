package com.petsignal.alert.dto;

import com.petsignal.alert.validator.ValidFileExtension;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class AddPhotosToAlertRequest {
  @Size(max = 5, message = "You can upload a maximum of 5 photos")
  @ValidFileExtension
  private List<String> photoFilenames;
}
