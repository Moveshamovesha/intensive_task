package com.example.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Ответ с информацией об ошибке")
public record ErrorResponse(

        @Schema(description = "HTTP-статус ошибки", example = "404")
        int status,

        @Schema(description = "Сообщение об ошибке", example = "Пользователь не найден: id=99")
        String message,

        @Schema(description = "Ошибки валидации по полям запроса")
        Map<String, String> validationErrors,

        @Schema(description = "Момент формирования ошибки", example = "2026-10-02T12:00:00")
        LocalDateTime timestamp
) {

    public static ErrorResponse of(int status, String message) {
        return new ErrorResponse(status, message, null, LocalDateTime.now());
    }

    public static ErrorResponse ofValidation(int status, Map<String, String> validationErrors) {
        return new ErrorResponse(status, "Ошибка валидации", validationErrors, LocalDateTime.now());
    }
}