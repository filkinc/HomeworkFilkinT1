package ru.filkin.aopproject.restaop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.filkin.aopproject.restaop.exception.NotFoundException;
import ru.filkin.aopproject.restaop.kafka.KafkaProducer;
import ru.filkin.aopproject.restaop.model.Task;
import ru.filkin.aopproject.restaop.model.TaskDTO;
import ru.filkin.aopproject.restaop.repository.TaskRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private KafkaProducer kafkaProducer;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskRepository);
        taskService.setKafkaProducer(kafkaProducer);
    }

    @Test
    public void whenCreateTask_thenReturnTaskDTO() {

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("Test Task");
        taskDTO.setDescription("Description");
        taskDTO.setUserId(10);
        taskDTO.setStatus("In Progress");

        Task savedTask = new Task();
        savedTask.setId(1);
        savedTask.setTitle("Test Task");
        savedTask.setDescription("Description");
        savedTask.setUserId(10);
        savedTask.setStatus("In Progress");

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        TaskDTO result = taskService.createTask(taskDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getTitle()).isEqualTo("Test Task");
        assertThat(result.getDescription()).isEqualTo("Description");
        assertThat(result.getUserId()).isEqualTo(10);
        assertThat(result.getStatus()).isEqualTo("In Progress");
    }

    @Test
    public void whenGetTaskById_thenReturnTaskDTO(){

        Task task = new Task();
        task.setId(1);
        task.setTitle("Test Task");
        task.setDescription("Description");
        task.setUserId(10);
        task.setStatus("In Progress");

        given(taskRepository.findById(1)).willReturn(Optional.of(task));

        TaskDTO taskDTO = taskService.getTaskById(1);

        assertThat(taskDTO).isNotNull();
        assertThat(taskDTO.getId()).isEqualTo(1);
        assertThat(taskDTO.getTitle()).isEqualTo("Test Task");
        assertThat(taskDTO.getDescription()).isEqualTo("Description");
        assertThat(taskDTO.getUserId()).isEqualTo(10);
        assertThat(taskDTO.getStatus()).isEqualTo("In Progress");
    }

    @Test
    public void whenGetTaskById_thenThrowNotFoundException() {

        given(taskRepository.findById(1)).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            taskService.getTaskById(1);
        });
    }

    @Test
    public void whenGetAllTasks_thenReturnTaskDTOList() {

        Task task1 = new Task();
        task1.setId(1);
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setUserId(10);
        task1.setStatus("In Progress");

        Task task2 = new Task();
        task2.setId(2);
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setUserId(20);
        task2.setStatus("Done");

        given(taskRepository.findAll()).willReturn(List.of(task1, task2));


        List<TaskDTO> result = taskService.getAllTasks();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Task 1");
        assertThat(result.get(1).getId()).isEqualTo(2);
        assertThat(result.get(1).getTitle()).isEqualTo("Task 2");
    }

    @Test
    public void whenUpdateTask_thenReturnUpdatedTaskDTO() {

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("Updated Task");
        taskDTO.setDescription("Updated Description");
        taskDTO.setUserId(123);
        taskDTO.setStatus("Done");

        Task task = new Task();
        task.setId(1);
        task.setTitle("Test Task");
        task.setDescription("Description");
        task.setUserId(123);
        task.setStatus("In Progress");

        Task updatedTask = new Task();
        updatedTask.setId(1);
        updatedTask.setTitle("Updated Task");
        updatedTask.setDescription("Updated Description");
        updatedTask.setUserId(123);
        updatedTask.setStatus("Done");

        given(taskRepository.findById(1)).willReturn(Optional.of(task));
        given(taskRepository.save(task)).willReturn(updatedTask);

        TaskDTO result = taskService.updateTask(1, taskDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getTitle()).isEqualTo("Updated Task");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        assertThat(result.getUserId()).isEqualTo(123);
        assertThat(result.getStatus()).isEqualTo("Done");

        verify(kafkaProducer).sendTaskUpdate(argThat(event ->
                event.getId() == 1 && "Done".equals(event.getNewStatus())));
    }

    @Test
    public void whenDeleteTask_thenTaskIsDeleted() {

        given(taskRepository.existsById(1)).willReturn(true);

        taskService.deleteTask(1);

        verify(taskRepository).deleteById(1);
    }

    @Test
    public void whenDeleteTask_thenThrowNotFoundException() {

        given(taskRepository.existsById(1)).willReturn(false);

        assertThrows(NotFoundException.class, () -> {
            taskService.deleteTask(1);
        });
    }
}