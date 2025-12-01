package ru.practicum.ewm.main.exception;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ApiError {

    private List<String> errors;
    private String message;
    private String reason;
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public ApiError() {
    }

    public ApiError(List<String> errors,
                    String message,
                    String reason,
                    String status,
                    LocalDateTime timestamp) {
        this.errors = errors;
        this.message = message;
        this.reason = reason;
        this.status = status;
        this.timestamp = timestamp;
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getMessage() {
        return message;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
