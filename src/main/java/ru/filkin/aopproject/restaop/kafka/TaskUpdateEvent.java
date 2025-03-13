package ru.filkin.aopproject.restaop.kafka;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TaskUpdateEvent {

    private int id;

    private String newStatus;
}
