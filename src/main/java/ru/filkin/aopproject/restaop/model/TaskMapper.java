//package ru.filkin.aopproject.restaop.model;
//
//import org.springframework.stereotype.Component;
//
//@Component
//public class TaskMapper {
//
//    public TaskDTO convertToDTO(Task task) {
//        return TaskDTO.builder()
//                .id(task.getId())
//                .title(task.getTitle())
//                .description(task.getDescription())
//                .userId(task.getUserId())
//                .status(task.getStatus())
//                .build();
//    }
//
//    public Task convertToEntity(TaskDTO taskDTO) {
//        return Task.builder()
//                .id(taskDTO.getId())
//                .title(taskDTO.getTitle())
//                .description(taskDTO.getDescription())
//                .userId(taskDTO.getUserId())
//                .status(taskDTO.getStatus())
//                .build();
//    }
//}
