package ru.filkin.aopproject.restaop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import ru.filkin.aopproject.restaop.exception.NotFoundException;
import ru.filkin.aopproject.restaop.model.Task;
import ru.filkin.aopproject.restaop.repository.TaskRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(String title, String description){
        Task task = Task.builder()
                .title(title)
                .description(description)
                .build();
        return taskRepository.save(task);
    }

    public Task getTaskById(int id){
        return taskRepository.findById(id).orElseThrow(() -> new NotFoundException("Task not found"));
    }

    public List<Task> getAllTasks(){
        return taskRepository.findAll();
    }

    public Task updateTask(int id, String title, String description, int userId){
        Optional<Task> taskOptional = taskRepository.findById(id);

        if(taskOptional.isEmpty()){
            throw new NotFoundException("Task not found");
        }

        Task task = taskOptional.get();
        task.setTitle(title);
        task.setDescription(description);
        task.setUserId(userId);

        return taskRepository.save(task);
    }

    public void deleteTask(int id){
        if (!taskRepository.existsById(id)){
            throw new NotFoundException("Task not found");
        }
        taskRepository.deleteById(id);
    }
}
