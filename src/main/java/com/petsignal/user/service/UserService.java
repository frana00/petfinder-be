package com.petsignal.user.service;

import com.petsignal.exception.ResourceNotFoundException;
import com.petsignal.user.dto.CreateUserRequest;
import com.petsignal.user.dto.UpdateUserRequest;
import com.petsignal.user.dto.UserResponse;
import com.petsignal.user.entity.User;
import com.petsignal.user.mapper.UserMapper;
import com.petsignal.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
  private static final String USER = "User";
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  public List<UserResponse> getAllUsers() {
    return userRepository.findAll().stream()
        .map(userMapper::toResponse)
        .toList();
  }

  public UserResponse findById(Long id) {
    return userRepository.findById(id)
        .map(userMapper::toResponse)
        .orElseThrow(() -> new ResourceNotFoundException(USER, "id", id));
  }


  public User findByUsername(String username) {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException(USER, "username", username));
  }

  @Transactional
  public UserResponse createUser(CreateUserRequest request) {
    User user = userMapper.toEntity(request);
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    return userMapper.toResponse(userRepository.save(user));
  }

  @Transactional
  public UserResponse updateUser(Long id, UpdateUserRequest request) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(USER, "id", id));

    userMapper.updateEntityFromRequest(request, user);
    return userMapper.toResponse(userRepository.save(user));
  }

  @Transactional
  public void deleteUser(Long id) {
    if (!userRepository.existsById(id)) {
      throw new ResourceNotFoundException(USER, "id", id);
    }
    userRepository.deleteById(id);
  }
} 