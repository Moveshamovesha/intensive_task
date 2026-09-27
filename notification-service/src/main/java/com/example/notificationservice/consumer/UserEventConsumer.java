package com.example.notificationservice.consumer;

import com.example.notificationservice.event.UserEvent;
import com.example.notificationservice.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(UserEventConsumer.class);

    private final EmailService emailService;

    public UserEventConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(
            topics = "${app.kafka.topic.user-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(UserEvent event) {
        log.info("Получено событие: {} для {}", event.getUserOperation(), event.getEmail());
        switch (event.getUserOperation()) {
            case CREATE -> emailService.sendEmail(
                    event.getEmail(),
                    "Аккаунт создан",
                    "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан."
            );
            case DELETE -> emailService.sendEmail(
                    event.getEmail(),
                    "Аккаунт удалён",
                    "Здравствуйте! Ваш аккаунт был удалён."
            );
        }
    }
}