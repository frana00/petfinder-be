package com.petsignal.notifications.service;

import com.petsignal.alert.entity.Alert;
import com.petsignal.alert.events.AlertEvent;
import com.petsignal.alert.service.AlertService;
import com.petsignal.emailnotifications.service.EmailNotificationService;
import com.petsignal.posts.entity.Post;
import com.petsignal.posts.event.PostEvent;
import com.petsignal.posts.service.PostService;
import com.petsignal.subscriptions.entity.Subscription;
import com.petsignal.subscriptions.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;


@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

  private final EmailNotificationService emailNotificationService;
  private final AlertService alertService;
  private final SubscriptionService subscriptionService;
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
    // Only process postcode-based notifications if the alert has a postcode
    if (alert.getPostCode() != null) {
      // subscribers
      var subscribers = subscriptionService.findByTypeAndPostCode(alert.getType(), alert.getPostCode()).stream()
          .map(Subscription::getUser);

      // alert owners
      var existingAlertOwners = alertService.getOppositeAlertsInPostcode(alert).stream()
          .map(Alert::getUser);

      Stream.concat(subscribers, existingAlertOwners)
          .distinct()
          .forEach(user -> emailNotificationService.sendNewAlertEmail(alert, user));
    } else {
      log.info("Alert {} has no postcode, skipping postcode-based notifications.", alert.getId());
      // Optionally, you might want to notify the alert owner directly, 
      // even if there's no postcode for broader notifications.
      // For example:
      // emailNotificationService.sendNewAlertEmail(alert, alert.getUser());
    }
  }

  public void handleAlertChange(AlertEvent.Type reason, Alert alert) {

    var posters = postService.findByAlertId(alert.getId()).stream()
        .map(Post::getUser)
        .distinct();

    // notify owner and posters
    Stream.concat(posters, Stream.of(alert.getUser()))
        .distinct()
        .forEach(user -> emailNotificationService.sendAlertChangeEmailNotification(reason, user, alert));

  }

}
