package com.petsignal.user.service;

import com.petsignal.exception.BadRequestException;
import com.petsignal.exception.ForbiddenException;
import com.petsignal.exception.ResourceNotFoundException;
import com.petsignal.security.CustomUserDetails;
import com.petsignal.user.dto.CreateUserRequest;
import com.petsignal.user.dto.UpdateProfileRequest;
import com.petsignal.user.dto.UpdateUserRequest;
import com.petsignal.user.dto.UserResponse;
import com.petsignal.user.entity.User;
import com.petsignal.user.mapper.UserMapper;
import com.petsignal.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

  public User findEntityById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(USER, "id", id));
  }


  public User findByUsername(String username) {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException(USER, "username", username));
  }

  public UserResponse findByUsernameResponse(String username) {
    return userRepository.findByUsername(username)
        .map(userMapper::toResponse)
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

  @Transactional
  public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
    // Obtener el usuario autenticado
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
      throw new ForbiddenException("User not authenticated");
    }

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    User authenticatedUser = userDetails.getUser();

    // Verificar que el usuario solo pueda actualizar su propio perfil
    // Los administradores pueden actualizar cualquier perfil
    if (!authenticatedUser.getId().equals(userId) && 
        !authenticatedUser.getRole().equals(User.Role.ADMIN)) {
      throw new ForbiddenException("You can only update your own profile");
    }

    // Buscar el usuario a actualizar
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(USER, "id", userId));

    // Validar email único si se está actualizando
    if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
      userRepository.findByEmail(request.getEmail())
          .filter(existingUser -> !existingUser.getId().equals(userId))
          .ifPresent(existingUser -> {
            throw new BadRequestException("Email already exists");
          });
      user.setEmail(request.getEmail());
    }

    // Actualizar subscription email si se proporciona
    if (request.getSubscriptionEmail() != null && !request.getSubscriptionEmail().trim().isEmpty()) {
      user.setSubscriptionEmail(request.getSubscriptionEmail());
    }

    // Actualizar phone number si se proporciona (puede ser null para eliminarlo)
    if (request.getPhoneNumber() != null) {
      if (request.getPhoneNumber().trim().isEmpty()) {
        user.setPhoneNumber(null);
      } else {
        user.setPhoneNumber(request.getPhoneNumber());
      }
    }

    // Guardar y retornar respuesta
    return userMapper.toResponse(userRepository.save(user));
  }
}