package ru.filkin.aopproject.restaop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.filkin.aopproject.restaop.model.Task;


@Service
public class KafkaProducer {

    private static final String TOPIC = "tasks-topic";  // Название топика

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTaskUpdate(int taskId, String newStatus) {
        String message = taskId + ":" + newStatus;
        kafkaTemplate.send(TOPIC, String.valueOf(taskId), message);
    }
}
