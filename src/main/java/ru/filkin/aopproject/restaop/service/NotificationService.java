package ru.filkin.aopproject.restaop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendNotification(int taskId, String newStatus) {
        log.info("Sending notification: Task ID={}, New Status={}", taskId, newStatus);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("filkin_test_address@mail.ru");
        message.setTo("filkin_test_address@mail.ru");
        message.setSubject("Task Status Updated");
        message.setText("Task ID: " + taskId + "\nNew Status: " + newStatus);

        try {
            mailSender.send(message);
            log.info("Email sent successfully!");
        } catch (Exception e) {
            log.error("Failed to send email: Task ID={}, New Status={}", taskId, newStatus, e);
        }
    }
}
