package ru.filkin.aopproject.restaop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.filkin.aopproject.restaop.PostgresContainer;
import ru.filkin.aopproject.restaop.exception.ErrorHandler;
import ru.filkin.aopproject.restaop.kafka.KafkaProducer;
import ru.filkin.aopproject.restaop.repository.TaskRepository;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration"
}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TaskControllerTest extends PostgresContainer {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private KafkaProducer kafkaProducer;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    public void whenGetTaskById_thenReturnTask() throws Exception {

        String taskJson = "{\"title\": \"Test Task\", \"description\": \"Description\", \"userId\": 10, \"status\": \"In Progress\"}";

        String response = mockMvc.perform(post("/tasks")
                        .contentType("application/json")
                        .content(taskJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Integer taskId = objectMapper.readTree(response).get("id").asInt();

        mockMvc.perform(get("/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.userId").value(10))
                .andExpect(jsonPath("$.status").value("In Progress"));
    }

    @Test
    public void whenGetTaskById_thenThrowNotFoundException() throws Exception {
        mockMvc.perform(get("/tasks/{id}", 1000))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task not found"))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"));
    }

    @Test
    public void whenGetAllTasks_thenReturnAllTasks() throws Exception {

        String taskJson1 = "{\"title\": \"Test Task1\", \"description\": \"Description\", \"userId\": 10, \"status\": \"In Progress\"}";
        mockMvc.perform(post("/tasks")
                        .contentType("application/json")
                        .content(taskJson1))
                .andExpect(status().isOk());

        String taskJson2 = "{\"title\": \"Test Task2\", \"description\": \"Description\", \"userId\": 20, \"status\": \"In Progress\"}";
        mockMvc.perform(post("/tasks")
                        .contentType("application/json")
                        .content(taskJson2))
                .andExpect(status().isOk());


        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Test Task1"))
                .andExpect(jsonPath("$[1].title").value("Test Task2"));
    }

    @Test
    public void whenUpdateTask_thenReturnUpdatedTask() throws Exception {

        String taskJson = "{\"title\": \"Test Task\", \"description\": \"Description\", \"userId\": 123, \"status\": \"In Progress\"}";
        String response = mockMvc.perform(post("/tasks")
                        .contentType("application/json")
                        .content(taskJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Integer taskId = objectMapper.readTree(response).get("id").asInt();


        String updatedTaskJson = "{\"title\": \"Updated Task\", \"description\": \"Updated Description\", \"userId\": 456, \"status\": \"Done\"}";
        mockMvc.perform(put("/tasks/{id}", taskId)
                        .contentType("application/json")
                        .content(updatedTaskJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.userId").value(456))
                .andExpect(jsonPath("$.status").value("Done"));

        verify(kafkaProducer).sendTaskUpdate(argThat(event ->
                event.getId() == taskId && "Done".equals(event.getNewStatus())));
    }

    @Test
    public void whenDeleteTask_thenTaskShouldBeDeleted() throws Exception {

        String taskJson = "{\"title\": \"Test Task\", \"description\": \"Description\", \"userId\": 123, \"status\": \"In Progress\"}";
        String response = mockMvc.perform(post("/tasks")
                        .contentType("application/json")
                        .content(taskJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Integer taskId = objectMapper.readTree(response).get("id").asInt();

        mockMvc.perform(delete("/tasks/{id}", taskId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));;

    }
}