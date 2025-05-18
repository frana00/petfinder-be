package com.petsignal.emailnotifications.service;

import com.petsignal.alert.entity.Alert;
import com.petsignal.alert.events.AlertEvent;
import com.petsignal.emailnotifications.EmailService;
import com.petsignal.emailnotifications.entity.EmailNotification;
import com.petsignal.emailnotifications.repository.EmailRepository;
import com.petsignal.emailnotifications.utils.EmailBuilder;
import com.petsignal.posts.entity.Post;
import com.petsignal.user.entity.User;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.petsignal.notifications.entity.NotificationStatus.*;
import static com.petsignal.notifications.entity.NotificationType.EMAIL;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService {

  private final EmailBuilder emailBuilder;
  private final EmailRepository emailRepository;
  private final EmailService emailService;


  @Async
  public void sendAlertChangeEmailNotification(AlertEvent.Type reason, User user, Alert alert) {

    EmailNotification emailNotification = createBasicEmailObject(alert, user);

    emailNotification.setSubject(getEmailSubject(reason));
    emailNotification.setBody(emailBuilder.buildAlertChangeBody(reason, user, alert));
    emailNotification.setStatus(PENDING);
    emailRepository.save(emailNotification);

    sendAndSaveEmail(emailNotification);
  }

  private void sendAndSaveEmail(EmailNotification emailNotification) {
    try {
      emailService.sendEmail(emailNotification.getSendTo(), emailNotification.getSubject(), emailNotification.getBody());
      emailNotification.setStatus(SENT);
    } catch (Exception e) {
      log.error("There was an error sending an email notification", e);
      emailNotification.setStatus(FAILED);
    } finally {
      emailRepository.save(emailNotification);
    }
  }

  public void sendNewAlertEmail(Alert alert, User user) {
    EmailNotification emailNotification = createBasicEmailObject(alert, user);
    emailNotification.setSubject(String.format("A new alert has been created in postcode %s", alert.getPostCode()));
    emailNotification.setBody(emailBuilder.buildNewAlertBody(user, alert));

    sendAndSaveEmail(emailNotification);
  }

  public void sendNewPostEmail(Post post, User user) {
    EmailNotification emailNotification = createBasicEmailObject(post.getAlert(), user);

    emailNotification.setSubject("New post activity");
    emailNotification.setBody(emailBuilder.buildNewPostBody(user, post));

    sendAndSaveEmail(emailNotification);

  }

  private EmailNotification createBasicEmailObject(Alert alert, User user) {
    String to = StringUtils.isNotBlank(user.getSubscriptionEmail()) ? user.getSubscriptionEmail() : user.getEmail();
    EmailNotification emailNotification = new EmailNotification();

    emailNotification.setUser(user);
    emailNotification.setAlert(alert);
    emailNotification.setType(EMAIL);

    emailNotification.setSendTo(to);

    return emailNotification;
  }

  private String getEmailSubject(AlertEvent.Type reason) {

    return switch (reason) {
      case UPDATED -> "An alert you are following has been updated";
      case DELETED -> "An alert you are following has been deleted";
      case RESOLVED -> "An alert you are following has been resolved";
      default -> throw new IllegalStateException("Unexpected value: " + reason);
    };
  }
}
