package com.petsignal.posts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDto {

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long id;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long alertId;

  @NotBlank(message = "Username is required")
  private String username;

  @NotBlank(message = "Content is required")
  @Size(max = 500, message = "Content must be at most 500 characters")
  private String content;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private LocalDateTime createdAt;

}
