package com.petsignal.notifications.service;

import com.petsignal.alert.entity.Alert;
import com.petsignal.alert.events.AlertEvent;
import com.petsignal.alert.service.AlertService;
import com.petsignal.emailnotifications.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

  private final EmailNotificationService emailNotificationService;
  private final AlertService alertService;

  @EventListener
  public void onAlertEvent(AlertEvent event) {
    Alert alert = event.alert();

    switch (event.type()) {
      case CREATED:
        handlerNewAlert(alert);
        break;
      case UPDATED, RESOLVED, DELETED:
        notifyStakeholders(event.type(), alert);
        break;
      default:
        log.warn("Unhandled alert event type: {}", event.type());
    }
  }

  private void handlerNewAlert(Alert alert) {
    alertService.getOppositeAlertsInPostcode(alert).stream()
        .map(Alert::getUser)
        .distinct()
        .forEach(user -> emailNotificationService.sendNewAlertEmail(alert, user));
  }

  public void notifyStakeholders(AlertEvent.Type reason, Alert alert) {

    // email alert owner
    emailNotificationService.sendAlertChangeEmailNotification(reason, alert.getUser(), alert);

  }

}
