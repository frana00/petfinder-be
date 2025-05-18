package com.petsignal.emailnotifications.repository;

import com.petsignal.emailnotifications.entity.EmailNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends JpaRepository<EmailNotification, Long> {
}
