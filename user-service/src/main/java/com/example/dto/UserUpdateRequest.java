package com.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Данные для обновления пользователя")
public record UserUpdateRequest(

        @Schema(description = "Новое имя пользователя", example = "Пётр", maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Имя не должно быть пустым")
        @Size(max = 100, message = "Имя не должно превышать 100 символов")
        String name,

        @Schema(description = "Новый email пользователя", example = "petr@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Email не должен быть пустым")
        @Email(message = "Некорректный формат email")
        String email,

        @Schema(description = "Новый возраст пользователя", example = "30", minimum = "0", maximum = "150",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Возраст обязателен")
        @Min(value = 0, message = "Возраст не может быть отрицательным")
        @Max(value = 150, message = "Возраст не может превышать 150")
        Integer age
) {
}
