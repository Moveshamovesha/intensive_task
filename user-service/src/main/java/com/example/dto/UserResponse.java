package com.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Пользователь, возвращаемый API")
public record UserResponse(

        @Schema(description = "Идентификатор пользователя", example = "1",
                accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "Имя пользователя", example = "Иван")
        String name,

        @Schema(description = "Email пользователя", example = "ivan@example.com")
        String email,

        @Schema(description = "Возраст пользователя", example = "25")
        Integer age,

        @Schema(description = "Дата и время создания пользователя", example = "2026-10-02T12:00:00",
                accessMode = Schema.AccessMode.READ_ONLY)
        LocalDateTime createdAt
) {
}