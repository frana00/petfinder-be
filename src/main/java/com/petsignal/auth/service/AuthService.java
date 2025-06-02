package com.petsignal.auth.service;

import com.petsignal.auth.dto.LoginRequest;
import com.petsignal.auth.dto.LoginResponse;
// UserService import will be removed if not used after changes
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
// Ensure JwtService is imported from the correct package if it was moved
// import com.petsignal.security.service.JwtService; 
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    // UserService removed as role is obtained from UserDetails authorities

    public LoginResponse login(LoginRequest request) {
        log.info("Attempting login for user: {}", request.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            // If authentication is successful, the principal should be UserDetails
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            // Generate JWT token
            String token = jwtService.generateToken(userDetails);
            log.info("Login successful, token generated for user: {}", username);

            // Fetch user role (or other details) if needed for LoginResponse
            // This part depends on how your UserDetails is structured or if you need to call UserService
            // For simplicity, if UserDetails has roles, extract from there.
            // Otherwise, load your User entity.
            String role = userDetails.getAuthorities().stream()
                            .findFirst() // Assuming one role, or adapt as needed
                            .map(grantedAuthority -> grantedAuthority.getAuthority())
                            .orElse("USER"); // Default role if none found

            return LoginResponse.builder()
                    .token(token)
                    .username(username)
                    .role(role) // Or fetch from your User entity
                    .build();

        } catch (BadCredentialsException e) {
            log.warn("Login failed for user {}: Invalid credentials", request.getUsername());
            throw e; // Re-throw to be handled by an exception handler or return ResponseEntity(HttpStatus.UNAUTHORIZED)
        } catch (Exception e) {
            log.error("Error during login for user {}: {}", request.getUsername(), e.getMessage(), e);
            // Consider a more specific exception or error response
            throw new RuntimeException("Login error: " + e.getMessage(), e);
        }
    }
}

