package ru.filkin.aopproject.restaop.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaProducer {

    private final KafkaTemplate<String, TaskUpdateEvent> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, TaskUpdateEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTaskUpdate(TaskUpdateEvent event) {
        log.info("Sending task update event: {}", event);
        kafkaTemplate.send("task-topic", event);
    }
}
