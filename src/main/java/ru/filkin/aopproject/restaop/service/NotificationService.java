package ru.filkin.aopproject.restaop.service;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void sendNotification(int taskId, String newStatus) {
        System.out.println("Sending notification: Task ID=" + taskId + ", New Status=" + newStatus);
    }
}
