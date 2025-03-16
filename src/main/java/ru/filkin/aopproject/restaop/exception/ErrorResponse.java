package ru.filkin.aopproject.restaop.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class ErrorResponse {
    private String message;
    private LocalDateTime timestamp;
    private String status;

    public ErrorResponse(String message, LocalDateTime timestamp, HttpStatus status) {
        this.message = message;
        this.timestamp = timestamp;
        this.status = status.name(); // Преобразуем HttpStatus в String
    }
}
