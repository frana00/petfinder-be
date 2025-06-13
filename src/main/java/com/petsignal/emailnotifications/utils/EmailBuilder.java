package com.petsignal.emailnotifications.utils;

import com.petsignal.alert.entity.Alert;
import com.petsignal.alert.events.AlertEvent;
import com.petsignal.posts.entity.Post;
import com.petsignal.user.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.petsignal.alert.events.AlertEvent.Type.DELETED;

@Component
public class EmailBuilder {

  @Value("${server.port}")
  private int serverPort;

  @Value("${server.servlet.context-path}")
  private String contextPath;

  @Value("${app.public-url:http://localhost}")
  private String publicUrl;

  private static final String USERNAME_PLACEHOLDER = "%username%";
  private static final String ALERT_INFO_PLACEHOLDER = "%alert_info%";

  private static final String TEMPLATE_ALERT_INFO = """
      Title: %alert_title%
      Description: %alert_description%
      Type: %alert_type%
      Status: %alert_status%
      %chip_number_line%
      """;

  private static final String TEMPLATE_CHANGE = """
      Hello %username%,
      
      The following alert has been %notification_reason%.
      %alert_info%
      
      %alert_link_line%
      """;

  private static final String TEMPLATE_NEW = """
      Hello %username%,
      
      A new alert has been created in %postcode%.
      %alert_info%
      
      You can see the full alert details here: %alert_link%
      """;

  private static final String TEMPLATE_POST = """
      Hello %username%,
      
      %poster_username% has commented on an alert you are following.
      %alert_info%
      
      %poster_username% wrote:
      %post_content%
      
      You can see the all alert activity here: %posts_link%
      """;


  public String buildAlertChangeBody(AlertEvent.Type reason, User user, Alert alert) {

    String alertLinkLine = !DELETED.equals(reason)
        ? "You can see the full alert details here: " + getAlertLink(alert)
        : "";

    return TEMPLATE_CHANGE
        .replace(USERNAME_PLACEHOLDER, user.getUsername())
        .replace(ALERT_INFO_PLACEHOLDER, fillInAlertInfo(alert))
        .replace("%notification_reason%", reason.name().toLowerCase())
        .replace("%alert_link_line%", alertLinkLine);
  }

  public String buildNewAlertBody(User user, Alert alert) {


    return TEMPLATE_NEW
        .replace(USERNAME_PLACEHOLDER, user.getUsername())
        .replace(ALERT_INFO_PLACEHOLDER, fillInAlertInfo(alert))
        .replace("%postcode%", (alert.getPostCode() != null && alert.getPostCode().getPostalCode() != null) ? alert.getPostCode().getPostalCode() : "N/A")
        .replace("%alert_link%", getAlertLink(alert));
  }

  public String buildNewPostBody(User recipient, Post newPost) {
    Alert alert = newPost.getAlert();

    return TEMPLATE_POST
        .replace(USERNAME_PLACEHOLDER, recipient.getUsername())
        .replace(ALERT_INFO_PLACEHOLDER, fillInAlertInfo(alert))
        .replace("%poster_username%", newPost.getUser().getUsername())
        .replace("%post_content%", newPost.getContent())
        .replace("%posts_link%", getPostsLink(alert));
  }

  private String getAlertLink(Alert alert) {
    return String.format("%s:%s%s/alerts/%d", publicUrl, serverPort, contextPath, alert.getId());
  }

  private String getPostsLink(Alert alert) {
    return String.format("%s:%s%s/alerts/%d/posts", publicUrl, serverPort, contextPath, alert.getId());
  }

  private String fillInAlertInfo(Alert alert) {
    String chipNumberLine = (alert.getChipNumber() != null && !alert.getChipNumber().isBlank())
        ? "Chip number: " + alert.getChipNumber()
        : "";

    return TEMPLATE_ALERT_INFO
        .replace("%alert_title%", alert.getTitle())
        .replace("%alert_description%", alert.getDescription())
        .replace("%alert_type%", alert.getType().name())
        .replace("%alert_status%", alert.getStatus().name())
        .replace("%chip_number_line%", chipNumberLine);
  }
}
