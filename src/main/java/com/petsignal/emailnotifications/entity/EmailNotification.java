package com.petsignal.emailnotifications.entity;

import com.petsignal.notifications.entity.Notification;
import com.petsignal.notifications.entity.NotificationStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "email_notifications")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EmailNotification extends Notification {

  @Enumerated(EnumType.STRING)
  @Column
  private NotificationStatus status;

  @Column(name = "send_to", length = 100)
  private String sendTo;

  @Column(length = 100)
  private String subject;

  @Column(columnDefinition = "TEXT")
  private String body;
}
