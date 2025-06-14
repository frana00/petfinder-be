package com.petsignal.posts.service;

import com.petsignal.alert.entity.Alert;
import com.petsignal.alert.service.AlertService;
import com.petsignal.exception.ForbiddenException;
import com.petsignal.exception.ResourceNotFoundException;
import com.petsignal.posts.dto.PostDto;
import com.petsignal.posts.dto.UpdatePostRequest;
import com.petsignal.posts.entity.Post;
import com.petsignal.posts.event.PostEvent;
import com.petsignal.posts.mapper.PostMapper;
import com.petsignal.posts.repository.PostRepository;
import com.petsignal.security.CustomUserDetails;
import com.petsignal.user.entity.User;
import com.petsignal.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final PostMapper postMapper;
  private final UserService userService;
  private final AlertService alertService;
  private final ApplicationEventPublisher eventPublisher;

  public PostDto createPost(Long alertId, PostDto request) {
    Post newPost = postMapper.toEntity(request);

    // validate alert
    Alert alert = alertService.findAlertEntityById(alertId);
    newPost.setAlert(alert);

    // validate user
    User user = userService.findByUsername(request.getUsername());
    newPost.setUser(user);

    eventPublisher.publishEvent(new PostEvent(newPost, alert));
    return postMapper.toDto(postRepository.save(newPost));
  }

  public List<Post> findByAlertId(Long alertId) {
    return postRepository.findByAlertId(alertId);
  }

  public List<PostDto> getPostsForAlert(Long alertId) {
    return findByAlertId(alertId).stream()
        .map(postMapper::toDto).toList();
  }

  public Post findPostById(Long postId) {
    return postRepository.findById(postId)
        .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
  }

  @Transactional
  public PostDto updatePost(Long postId, UpdatePostRequest request) {
    Post post = findPostById(postId);
    String currentUsername = getCurrentUsername();
    
    // Solo el autor del post puede editarlo
    if (!post.getUser().getUsername().equals(currentUsername)) {
      throw new ForbiddenException("You are not authorized to edit this post");
    }
    
    post.setContent(request.getContent());
    Post updatedPost = postRepository.save(post);
    
    log.info("Post with ID {} updated by user {}", postId, currentUsername);
    return postMapper.toDto(updatedPost);
  }

  @Transactional
  public void deletePost(Long postId) {
    Post post = findPostById(postId);
    String currentUsername = getCurrentUsername();
    
    // El autor del post O el dueño de la alerta pueden eliminar el post
    boolean isPostAuthor = post.getUser().getUsername().equals(currentUsername);
    boolean isAlertOwner = post.getAlert().getUser().getUsername().equals(currentUsername);
    
    if (!isPostAuthor && !isAlertOwner) {
      throw new ForbiddenException("You are not authorized to delete this post");
    }
    
    postRepository.delete(post);
    log.info("Post with ID {} deleted by user {}", postId, currentUsername);
  }

  private String getCurrentUsername() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
      return userDetails.getUsername();
    }
    throw new RuntimeException("No authenticated user found");
  }
}
