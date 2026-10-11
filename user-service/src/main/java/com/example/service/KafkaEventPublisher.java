package com.example.service;

import com.example.event.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class KafkaEventPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(KafkaEventPublisher.class);

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;
    private final CircuitBreaker circuitBreaker;

    @Value("${app.kafka.topic.user-events}")
    private String userEventsTopic;

    public KafkaEventPublisher(
            KafkaTemplate<String, UserEvent> kafkaTemplate,
            CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        this.kafkaTemplate = kafkaTemplate;
        this.circuitBreaker = circuitBreakerFactory.create("kafka-publisher");
    }

    public void publish(UserEvent event) {
        circuitBreaker.run(() -> {
            try {
                kafkaTemplate.send(userEventsTopic, event)
                        .get(5, TimeUnit.SECONDS);

                log.info("Kafka-событие успешно отправлено: {}", event);
                return null;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(
                        "Ожидание отправки Kafka-события прервано", e);

            } catch (ExecutionException | TimeoutException e) {
                throw new IllegalStateException(
                        "Не удалось отправить Kafka-событие", e);
            }
        }, error -> {
            log.error("Circuit Breaker: ошибка отправки Kafka-события", error);
            throw new IllegalStateException(
                    "Kafka-событие не опубликовано", error);
        });
    }
}
