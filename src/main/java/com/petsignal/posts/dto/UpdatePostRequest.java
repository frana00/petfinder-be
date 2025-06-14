package com.petsignal.posts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePostRequest {

  @NotBlank(message = "Content is required")
  @Size(max = 500, message = "Content must be at most 500 characters")
  private String content;
}
