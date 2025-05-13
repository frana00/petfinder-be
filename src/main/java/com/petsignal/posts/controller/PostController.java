package com.petsignal.posts.controller;

import com.petsignal.posts.dto.PostDto;
import com.petsignal.posts.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/alerts/{alertId}/posts")
@RequiredArgsConstructor
public class PostController {

  private final PostService postService;

  @PostMapping
  public ResponseEntity<PostDto> createPost(
      @PathVariable Long alertId,
      @RequestBody
      PostDto request) {

    PostDto created = postService.createPost(alertId, request);
    return ResponseEntity.status(CREATED).body(created);
  }

  @GetMapping
  public ResponseEntity<List<PostDto>> getPostsForAlert(@PathVariable Long alertId) {
    return ResponseEntity.ok(postService.getPostsForAlert(alertId));
  }
}
