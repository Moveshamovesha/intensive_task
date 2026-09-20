package com.example.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        int status,
        String message,
        Map<String, String> validationErrors,
        LocalDateTime timestamp
) {

    public static ErrorResponse of(int status, String message) {
        return new ErrorResponse(status, message, null, LocalDateTime.now());
    }

    public static ErrorResponse ofValidation(int status, Map<String, String> validationErrors) {
        return new ErrorResponse(status, "Ошибка валидации", validationErrors, LocalDateTime.now());
    }
}
