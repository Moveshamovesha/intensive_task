package com.example.notificationservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailRequest(

        @NotBlank(message = "Email не должен быть пустым")
        @Email(message = "Некорректный формат email")
        String to,

        @NotBlank(message = "Тема не должна быть пустой")
        String subject,

        @NotBlank(message = "Текст не должен быть пустым")
        String text
) {
}