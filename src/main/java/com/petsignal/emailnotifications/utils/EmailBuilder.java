package com.petsignal.emailnotifications.utils;

import com.petsignal.alert.entity.Alert;
import com.petsignal.alert.events.AlertEvent;
import com.petsignal.user.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailBuilder {

  @Value("${server.port}")
  private int serverPort;

  @Value("${server.servlet.context-path}")
  private String contextPath;

  private static final String TEMPLATE_CHANGE = """
      Hello %username%,
      
      The following alert has been %notification_reason%.
      Title: %alert_title%
      Description: %alert_description%
      Type: %alert_type%
      Status: %alert_status%
      %chip_number_line%
      
      You can see the full alert details here: %alert_link%
      """;

  private static final String TEMPLATE_NEW = """
      Hello %username%,
      
      A new alert has been created in the same postcode as your current active one.
      Title: %alert_title%
      Description: %alert_description%
      Type: %alert_type%
      Status: %alert_status%
      %chip_number_line%
      
      You can see the full alert details here: %alert_link%
      """;


  public String buildAlertChangeBody(AlertEvent.Type reason, User user, Alert alert) {

    String chipNumberLine = (alert.getChipNumber() != null && !alert.getChipNumber().isBlank())
        ? "Chip number: " + alert.getChipNumber()
        : "";

    return TEMPLATE_CHANGE
        .replace("%username%", user.getUsername())
        .replace("%notification_reason%", reason.name().toLowerCase())
        .replace("%alert_title%", alert.getTitle())
        .replace("%alert_description%", alert.getDescription())
        .replace("%alert_type%", alert.getType().name())
        .replace("%alert_status%", alert.getStatus().name())
        .replace("%chip_number_line%", chipNumberLine)
        .replace("%alert_link%", getAlertLink(alert));
  }

  public String buildNewAlertBody(User user, Alert alert) {

    String chipNumberLine = (alert.getChipNumber() != null && !alert.getChipNumber().isBlank())
        ? "Chip number: " + alert.getChipNumber()
        : "";

    return TEMPLATE_NEW
        .replace("%username%", user.getUsername())
        .replace("%alert_title%", alert.getTitle())
        .replace("%alert_description%", alert.getDescription())
        .replace("%alert_type%", alert.getType().name())
        .replace("%alert_status%", alert.getStatus().name())
        .replace("%chip_number_line%", chipNumberLine)
        .replace("%alert_link%", getAlertLink(alert));
  }

  private String getAlertLink(Alert alert) {
    String host = "http://localhost"; // You can parametrize host if needed later
    return String.format("%s:%s%s/alerts/%d", host, serverPort, contextPath, alert.getId());
  }


}
