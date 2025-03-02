package ru.filkin.aopproject.restaop.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KafkaConsumer {

    private final NotificationService notificationService;

    public KafkaConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "tasks-topic", groupId = "task-group")
    public void listen(List<String> messages) {
        for (String message : messages) {
            String[] parts = message.split(":");
            int taskId = Integer.parseInt(parts[0]);
            String newStatus = parts[1];
            notificationService.sendNotification(taskId, newStatus);
        }
    }
}
