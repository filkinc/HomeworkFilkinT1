package ru.filkin.aopproject.restaop.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.filkin.aopproject.restaop.PostgresContainer;
import ru.filkin.aopproject.restaop.model.TaskDTO;
import ru.filkin.aopproject.restaop.service.TaskService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
public class TaskControllerTest extends PostgresContainer {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Test
    public void whenGetTaskById_thenReturnTask() throws Exception {

        TaskDTO taskDTO = new TaskDTO(1, "Test Task", "Description", 123,"In Progress");
        when(taskService.getTaskById(1)).thenReturn(taskDTO);

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.userId").value(123))
                .andExpect(jsonPath("$.status").value("In Progress"));
    }
}