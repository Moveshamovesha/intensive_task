package com.example.notificationservice;

import com.example.notificationservice.event.UserEvent;
import com.example.notificationservice.event.UserOperation;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(topics = "user-events", partitions = 1)
class NotificationFlowIT {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test", "test"))
            .withPerMethodLifecycle(false);

    @Autowired
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Test
    void kafkaCreateEvent_sendsCreatedEmail() {
        kafkaTemplate.send("user-events", new UserEvent("ivan@test.com", UserOperation.CREATE));

        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> {
            assertThat(greenMail.getReceivedMessages()).hasSize(1);
            assertThat(greenMail.getReceivedMessages()[0].getAllRecipients()[0].toString())
                    .isEqualTo("ivan@test.com");
            assertThat(greenMail.getReceivedMessages()[0].getContent().toString())
                    .contains("Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.");
        });
    }

    @Test
    void kafkaDeleteEvent_sendsDeletedEmail() {
        kafkaTemplate.send("user-events", new UserEvent("ivan@test.com", UserOperation.DELETE));

        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> {
            assertThat(greenMail.getReceivedMessages()).hasSize(1);
            assertThat(greenMail.getReceivedMessages()[0].getContent().toString())
                    .contains("Здравствуйте! Ваш аккаунт был удалён.");
        });
    }
}
