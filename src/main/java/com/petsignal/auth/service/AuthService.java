package com.petsignal.auth.service;

import com.petsignal.auth.dto.LoginRequest;
import com.petsignal.auth.dto.LoginResponse;
import com.petsignal.user.entity.User;
import com.petsignal.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    
    public LoginResponse login(LoginRequest request) {
        try {
            // Authenticate the user using Spring Security
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            // If authentication is successful, get user details
            User user = userService.findByUsername(request.getUsername());
            
            log.info("User {} logged in successfully", request.getUsername());
            
            // In a real implementation, you would generate a JWT token here
            // For now, return a simulated token
            return LoginResponse.builder()
                    .token("simulated-jwt-token-for-" + user.getUsername())
                    .username(user.getUsername())
                    .role(user.getRole().name())
                    .build();
                    
        } catch (BadCredentialsException e) {
            log.warn("Failed login attempt for username: {}", request.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        } catch (Exception e) {
            log.error("Login error for username: {}", request.getUsername(), e);
            throw new BadCredentialsException("Authentication failed");
        }
    }
}
