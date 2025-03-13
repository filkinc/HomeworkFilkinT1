package ru.filkin.aopproject.restaop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.filkin.aopproject.restaop.exception.NotFoundException;
import ru.filkin.aopproject.restaop.kafka.KafkaProducer;
import ru.filkin.aopproject.restaop.kafka.TaskUpdateEvent;
import ru.filkin.aopproject.restaop.model.Task;
import ru.filkin.aopproject.restaop.model.TaskDTO;
import ru.filkin.aopproject.restaop.model.TaskMapper;
import ru.filkin.aopproject.restaop.repository.TaskRepository;
import ru.filkin.starter.loggingspringbootstarter.annotation.CustomAnnotation;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    private KafkaProducer kafkaProducer;

    @Autowired
    public void setKafkaProducer(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    private TaskMapper taskMapper;

    @Autowired
    public void setTaskMapper(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    @CustomAnnotation
    public TaskDTO createTask(TaskDTO taskDTO) {
        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setUserId(taskDTO.getUserId());
        task.setStatus(taskDTO.getStatus());
        Task savedTask = taskRepository.save(task);
        return taskMapper.convertToDTO(savedTask);
    }

    @CustomAnnotation
    public TaskDTO getTaskById(int id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found"));
        return taskMapper.convertToDTO(task);
    }

    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(taskMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    @CustomAnnotation
    public TaskDTO updateTask(int id, TaskDTO taskDTO) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        boolean statusChanged = !taskDTO.getStatus().equals(task.getStatus());

        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setUserId(taskDTO.getUserId());
        task.setStatus(taskDTO.getStatus());
        Task updatedTask = taskRepository.save(task);

        if (statusChanged) {
            TaskUpdateEvent event = new TaskUpdateEvent(id, taskDTO.getStatus());
            kafkaProducer.sendTaskUpdate(event);
        }
        return taskMapper.convertToDTO(updatedTask);
    }

    @CustomAnnotation
    public void deleteTask(int id) {
        if (!taskRepository.existsById(id)) {
            throw new NotFoundException("Task not found");
        }
        taskRepository.deleteById(id);
    }
}
