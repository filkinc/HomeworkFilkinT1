package ru.filkin.aopproject.restaop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.filkin.aopproject.restaop.model.TaskDTO;
import ru.filkin.aopproject.restaop.service.TaskService;
import ru.filkin.starter.loggingspringbootstarter.annotation.CustomAnnotation;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @CustomAnnotation
    @PostMapping
    public TaskDTO createTask(@RequestBody TaskDTO taskDTO) {
        return taskService.createTask(taskDTO);
    }

    @CustomAnnotation
    @GetMapping("/{id}")
    public TaskDTO getTaskById(@PathVariable int id) {
        return taskService.getTaskById(id);
    }

    @CustomAnnotation
    @GetMapping
    public List<TaskDTO> getAllTasks() {
        return taskService.getAllTasks();
    }

    @CustomAnnotation
    @PutMapping("/{id}")
    public TaskDTO updateTask(@PathVariable int id, @RequestBody TaskDTO taskDTO) {
        return taskService.updateTask(id, taskDTO);
    }

    @CustomAnnotation
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable int id) {
        taskService.deleteTask(id);
    }
}
