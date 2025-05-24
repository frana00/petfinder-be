package com.petsignal.subscriptions.repository;

import com.petsignal.subscriptions.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
}
