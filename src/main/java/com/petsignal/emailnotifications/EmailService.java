package com.petsignal.emailnotifications;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender mailSender;

  @Value("${notifications.email.from}")
  private String fromAddress;

  public void sendEmail(String to, String subject, String htmlBody) {
    MimeMessage message = mailSender.createMimeMessage();
    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setFrom(fromAddress);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(htmlBody, true); // true = HTML
      mailSender.send(message);
    } catch (MessagingException e) {
      throw new RuntimeException("Error al enviar email HTML", e);
    }
  }
}
