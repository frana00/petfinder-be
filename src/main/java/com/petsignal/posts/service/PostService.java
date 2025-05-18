package com.petsignal.posts.service;

import com.petsignal.alert.entity.Alert;
import com.petsignal.alert.service.AlertService;
import com.petsignal.posts.dto.PostDto;
import com.petsignal.posts.entity.Post;
import com.petsignal.posts.event.PostEvent;
import com.petsignal.posts.mapper.PostMapper;
import com.petsignal.posts.repository.PostRepository;
import com.petsignal.user.entity.User;
import com.petsignal.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

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
}
