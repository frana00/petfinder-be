package com.petsignal.subscriptions.entity;

import com.petsignal.alert.entity.AlertType;
import com.petsignal.countries.entity.Country;
import com.petsignal.notifications.entity.NotificationType;
import com.petsignal.postcodes.entity.PostCode;
import com.petsignal.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "subscriptions")
public class Subscription {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "country_code", nullable = false)
  private Country country;

  @Enumerated(EnumType.STRING)
  @Column(name = "alert_type")
  private AlertType alertType;

  @Enumerated(EnumType.STRING)
  @Column(name = "notification_type")
  private NotificationType notificationType;

  @ManyToMany
  @JoinTable(
      name = "subscriptions_postal_codes",
      joinColumns = @JoinColumn(name = "subscription_id"),
      inverseJoinColumns = @JoinColumn(name = "postal_code_id")
  )
  private Set<PostCode> postCodes = new HashSet<>();
}
