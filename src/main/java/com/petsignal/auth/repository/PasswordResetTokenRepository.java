package com.petsignal.auth.repository;

import com.petsignal.auth.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Stream<PasswordResetToken> findAllByExpiresAtBefore(LocalDateTime now);

    void deleteAllByExpiresAtBefore(LocalDateTime now);

    long countByEmailAndCreatedAtAfter(String email, LocalDateTime timestamp);
}
