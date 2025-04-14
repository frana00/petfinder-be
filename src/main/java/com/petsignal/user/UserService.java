package com.petsignal.user;

import com.petsignal.user.dto.UserRequest;
import com.petsignal.user.dto.UserResponseDto;
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
    
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
            .map(userMapper::toResponseDto)
            .toList();
    }

    public UserResponseDto findById(Integer id) {
        return userRepository.findById(id)
            .map(userMapper::toResponseDto)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }
    
    @Transactional
    public UserResponseDto createUser(UserRequest request) {
        User user = userMapper.toEntity(request);
        return userMapper.toResponseDto(userRepository.save(user));
    }
    
    @Transactional
    public UserResponseDto updateUser(Integer id, UserRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        
        userMapper.updateEntityFromRequest(request, user);
        return userMapper.toResponseDto(userRepository.save(user));
    }
    
    @Transactional
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
} 