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
    private final String fromEmail;
    private final String toEmail;
    private final String emailSubject;
    private final String emailTextTemplate;


    public NotificationService(
            JavaMailSender mailSender,
            @Value("${spring.mail.from}") String fromEmail,
            @Value("${spring.mail.to}") String toEmail,
            @Value("${spring.mail.subject}") String emailSubject,
            @Value("${spring.mail.text}") String emailTextTemplate
    ) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
        this.toEmail = toEmail;
        this.emailSubject = emailSubject;
        this.emailTextTemplate = emailTextTemplate;
    }

    public void sendNotification(TaskUpdateEvent event) {
        log.info("Sending notification: Task ID={}, New Status={}", event.getId(), event.getNewStatus());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(emailSubject);

        String emailText = String.format(emailTextTemplate, event.getId(), event.getNewStatus());
        message.setText(emailText);

        try {
            mailSender.send(message);
            log.info("Email sent successfully!");
        } catch (Exception e) {
            log.error("Failed to send email: Task ID={}, New Status={}", event.getId(), event.getNewStatus(), e);
        }
    }
}
