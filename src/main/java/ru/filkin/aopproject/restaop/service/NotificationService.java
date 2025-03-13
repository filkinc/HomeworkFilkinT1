package ru.filkin.aopproject.restaop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.filkin.aopproject.restaop.kafka.TaskUpdateEvent;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Service
public class NotificationService {

    private final JavaMailSender mailSender;
    private final ConfigurationMail configurationMail;


    public NotificationService(JavaMailSender mailSender, ConfigurationMail configurationMail) {
        this.mailSender = mailSender;
        this.configurationMail = configurationMail;
    }

    public void sendNotification(TaskUpdateEvent event) {
        log.info("Sending notification: Task ID={}, New Status={}", event.getId(), event.getNewStatus());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(configurationMail.getFrom());
        message.setTo(configurationMail.getTo());
        message.setSubject(configurationMail.getSubject());

        String emailText = String.format(configurationMail.getText(), event.getId(), event.getNewStatus());
        message.setText(emailText);

        try {
            mailSender.send(message);
            log.info("Email sent successfully!");
        } catch (Exception e) {
            log.error("Failed to send email: Task ID={}, New Status={}", event.getId(), event.getNewStatus(), e);
        }
    }
}
