package com.petsignal.auth.service;

import com.petsignal.auth.dto.VerifyTokenResponse;
import com.petsignal.auth.entity.PasswordResetToken;
import com.petsignal.auth.repository.PasswordResetTokenRepository;
import com.petsignal.emailnotifications.EmailService; // Assuming this is the correct EmailService
import com.petsignal.user.entity.User;
import com.petsignal.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder; // Assuming PasswordEncoder bean is available
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService; // Injected EmailService
    private final PasswordEncoder passwordEncoder; // Injected PasswordEncoder
    private final SpringTemplateEngine templateEngine; // For processing HTML email template

    @Value("${password-reset.token-expiry-minutes:15}")
    private int tokenExpiryMinutes;

    // ✅ Nueva configuración para deep links
    @Value("${password-reset.frontend-scheme:petsignal://}")
    private String frontendScheme;

    @Value("${password-reset.frontend-path:reset-password}")
    private String frontendPath;

    // Mantener para compatibilidad (deprecado)
    @Value("${password-reset.frontend-base-url}")
    private String frontendBaseUrl;

    @Value("${password-reset.max-attempts-per-hour:3}")
    private int maxAttemptsPerHour;


    @Transactional
    public void requestPasswordReset(String email, HttpServletRequest request) {
        String clientIp = getClientIp(request);
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

        long attemptsInLastHour = tokenRepository.countByEmailAndCreatedAtAfter(email, oneHourAgo);
        if (attemptsInLastHour >= maxAttemptsPerHour) {
            log.warn("Password reset rate limit exceeded for email: {} from IP: {}", email, clientIp);
            // Still return a generic success message to prevent email enumeration
            return;
        }

        userRepository.findByEmail(email).ifPresent(user -> {
            String plainVerifier = UUID.randomUUID().toString();
            String hashedVerifier = passwordEncoder.encode(plainVerifier);
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(tokenExpiryMinutes);

            PasswordResetToken token = new PasswordResetToken(hashedVerifier, user, email, expiresAt, clientIp);
            PasswordResetToken savedToken = tokenRepository.save(token);

            String tokenIdentifier = savedToken.getId() + ":" + plainVerifier;
            // ✅ Usar deep link en lugar de URL HTTP
            String resetUrl = frontendScheme + frontendPath + "?token=" + tokenIdentifier;

            sendPasswordResetEmail(user.getEmail(), resetUrl);
            log.info("Password reset requested for email: {} from IP: {}. Token ID: {}", email, clientIp, savedToken.getId());
        });

        // Always log an attempt, even if user not found, for security auditing (though less detailed)
        if (userRepository.findByEmail(email).isEmpty()) {
            log.info("Password reset attempt for non-existent email: {} from IP: {}", email, clientIp);
        }
    }

    private void sendPasswordResetEmail(String toEmail, String resetUrl) {
        Context context = new Context();
        context.setVariable("RESET_URL", resetUrl);
        // Assuming your template is in 'templates/email/password-reset-email-template.html'
        String htmlBody = templateEngine.process("email/password-reset-email-template", context);

        try {
            emailService.sendEmail(toEmail, "Recupera tu contraseña - PetSignal", htmlBody);
            log.info("Password reset email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", toEmail, e);
            // Depending on requirements, might re-throw or handle silently
        }
    }

    @Transactional(readOnly = true)
    public VerifyTokenResponse verifyResetToken(String tokenIdentifier) {
        try {
            String[] parts = tokenIdentifier.split(":");
            if (parts.length != 2) {
                return new VerifyTokenResponse(false, null);
            }
            Long tokenId = Long.parseLong(parts[0]);
            String plainVerifier = parts[1];

            return tokenRepository.findById(tokenId)
                .map(token -> {
                    if (token.getUsedAt() != null || token.getExpiresAt().isBefore(LocalDateTime.now())) {
                        return new VerifyTokenResponse(false, null); // Token used or expired
                    }
                    boolean isValid = passwordEncoder.matches(plainVerifier, token.getTokenHash());
                    return new VerifyTokenResponse(isValid, isValid ? token.getEmail() : null);
                })
                .orElse(new VerifyTokenResponse(false, null)); // Token not found
        } catch (NumberFormatException e) {
            log.warn("Invalid token format received for verification: {}", tokenIdentifier);
            return new VerifyTokenResponse(false, null);
        }
    }

    @Transactional
    public boolean resetPassword(String tokenIdentifier, String newPassword) {
        // Password strength validation (min 8 characters) is handled by OpenAPI spec + @Valid in controller
        try {
            String[] parts = tokenIdentifier.split(":");
            if (parts.length != 2) {
                return false;
            }
            Long tokenId = Long.parseLong(parts[0]);
            String plainVerifier = parts[1];

            Optional<PasswordResetToken> tokenOptional = tokenRepository.findById(tokenId);
            if (tokenOptional.isEmpty()) {
                return false; // Token not found
            }

            PasswordResetToken token = tokenOptional.get();
            if (token.getUsedAt() != null || token.getExpiresAt().isBefore(LocalDateTime.now())) {
                return false; // Token used or expired
            }

            if (!passwordEncoder.matches(plainVerifier, token.getTokenHash())) {
                return false; // Verifier mismatch
            }

            User user = token.getUser();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            token.setUsedAt(LocalDateTime.now());
            tokenRepository.save(token);
            log.info("Password successfully reset for user ID: {}. Token ID: {}", user.getId(), token.getId());
            return true;
        } catch (NumberFormatException e) {
            log.warn("Invalid token format received for password reset: {}", tokenIdentifier);
            return false;
        }
    }

    @Scheduled(fixedRateString = "${password-reset.cleanup-interval-minutes:30}m") // e.g., 1800000 ms for 30 mins
    @Transactional
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        log.info("Running scheduled job to clean up expired password reset tokens older than {}", now);
        long countBefore = tokenRepository.count();
        tokenRepository.deleteAllByExpiresAtBefore(now);
        long countAfter = tokenRepository.count();
        log.info("Expired token cleanup complete. Deleted {} tokens.", countBefore - countAfter);
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || "unknown".equalsIgnoreCase(xfHeader)) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
