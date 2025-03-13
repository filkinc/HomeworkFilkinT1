package ru.filkin.aopproject.restaop.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class TaskDTO {

    private int id;

    private String title;

    private String description;

    private int userId;

    private String status;
}
