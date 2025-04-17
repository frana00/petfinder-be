package com.petsignal.user.service;

import com.petsignal.user.dto.UserRequest;
import com.petsignal.user.dto.UserResponse;
import com.petsignal.user.entity.User;
import com.petsignal.user.mapper.UserMapper;
import com.petsignal.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private static final String USER_NOT_FOUND_MESSAGE = "User not found with id: %d";
    
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(userMapper::toResponse)
            .toList();
    }

    public UserResponse findById(Long id) {
        return userRepository.findById(id)
            .map(userMapper::toResponse)
            .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE.formatted(id)));
    }
    
    @Transactional
    public UserResponse createUser(UserRequest request) {
        User user = userMapper.toEntity(request);
        return userMapper.toResponse(userRepository.save(user));
    }
    
    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE.formatted(id)));
        
        userMapper.updateEntityFromRequest(request, user);
        return userMapper.toResponse(userRepository.save(user));
    }
    
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException(USER_NOT_FOUND_MESSAGE.formatted(id));
        }
        userRepository.deleteById(id);
    }
} 