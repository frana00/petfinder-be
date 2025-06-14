package com.petsignal.auth.controller;

import com.petsignal.auth.dto.ForgotPasswordRequest;
import com.petsignal.auth.dto.ResetPasswordRequest;
import com.petsignal.auth.dto.VerifyTokenResponse;
import com.petsignal.auth.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Operations related to user authentication and password management")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @Operation(summary = "Request password reset", operationId = "requestPasswordReset")
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> requestPasswordReset(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest, HttpServletRequest request) {
        passwordResetService.requestPasswordReset(forgotPasswordRequest.getEmail(), request);
        // Always return 200 OK for security reasons, to prevent email enumeration
        return ResponseEntity.ok(Map.of("message", "Si el email está registrado, recibirás instrucciones para recuperar tu contraseña"));
    }

    @Operation(summary = "Verify if reset token is valid", operationId = "verifyResetToken")
    @GetMapping("/verify-reset-token/{token}")
    public ResponseEntity<VerifyTokenResponse> verifyResetToken(@PathVariable String token) {
        VerifyTokenResponse response = passwordResetService.verifyResetToken(token);
        if (response.isValid()) {
            return ResponseEntity.ok(response);
        }
        // Consider if a more specific error (e.g., 400) is appropriate based on OpenAPI spec for invalid/expired
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new VerifyTokenResponse(false, null)); 
    }

    @Operation(summary = "Reset password with token", operationId = "resetPassword")
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        boolean success = passwordResetService.resetPassword(resetPasswordRequest.getToken(), resetPasswordRequest.getNewPassword());
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Contraseña actualizada exitosamente"));
        }
        // Based on OpenAPI, 400 for invalid token or weak password
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Token inválido o contraseña no cumple los requisitos."));
    }
}
