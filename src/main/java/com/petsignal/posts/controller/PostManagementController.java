package com.petsignal.posts.controller;

import com.petsignal.posts.dto.PostDto;
import com.petsignal.posts.dto.UpdatePostRequest;
import com.petsignal.posts.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostManagementController {

  private final PostService postService;

  @PutMapping("/{postId}")
  public ResponseEntity<PostDto> updatePost(
      @PathVariable Long postId,
      @Valid @RequestBody UpdatePostRequest request) {

    PostDto updated = postService.updatePost(postId, request);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{postId}")
  public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
    postService.deletePost(postId);
    return ResponseEntity.noContent().build();
  }
}
