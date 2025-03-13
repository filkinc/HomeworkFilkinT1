package ru.filkin.aopproject.restaop.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.filkin.aopproject.restaop.service.NotificationService;

import java.util.List;

@Service
public class KafkaConsumer {

    private final NotificationService notificationService;

    public KafkaConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "${spring.kafka.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(List<TaskUpdateEvent> eventList) {
        for (TaskUpdateEvent event : eventList) {
            notificationService.sendNotification(event);
        }
    }
}
