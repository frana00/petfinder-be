package com.petsignal.notifications.service;

import com.petsignal.alert.entity.Alert;
import com.petsignal.alert.events.AlertEvent;
import com.petsignal.alert.service.AlertService;
import com.petsignal.emailnotifications.service.EmailNotificationService;
import com.petsignal.posts.entity.Post;
import com.petsignal.posts.event.PostEvent;
import com.petsignal.posts.service.PostService;
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
  private final PostService postService;

  @EventListener
  public void onAlertEvent(AlertEvent event) {
    Alert alert = event.alert();

    switch (event.type()) {
      case CREATED:
        handlerNewAlert(alert);
        break;
      case UPDATED, RESOLVED, DELETED:
        handleAlertChange(event.type(), alert);
        break;
      default:
        log.warn("Unhandled alert event type: {}", event.type());
    }
  }

  @EventListener
  public void onPostEvent(PostEvent event) {
    // send notification to alert owner
    emailNotificationService.sendNewPostEmail(event.post(), event.post().getAlert().getUser());

    // send notifications to other posters
    postService.findByAlertId(event.alert().getId()).stream()
        .map(Post::getUser)
        .distinct()
        .forEach(user -> emailNotificationService.sendNewPostEmail(event.post(), user));
  }

  private void handlerNewAlert(Alert alert) {
    alertService.getOppositeAlertsInPostcode(alert).stream()
        .map(Alert::getUser)
        .distinct()
        .forEach(user -> emailNotificationService.sendNewAlertEmail(alert, user));
  }

  public void handleAlertChange(AlertEvent.Type reason, Alert alert) {
    // email alert owner
    emailNotificationService.sendAlertChangeEmailNotification(reason, alert.getUser(), alert);

  }

}
