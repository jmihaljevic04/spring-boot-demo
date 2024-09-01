package com.pet.pethubbatch.infrastructure.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

@Slf4j
public final class RabbitMqListenerLogger {

    private RabbitMqListenerLogger() {
        throw new IllegalAccessError("Utility class!");
    }

    public static void logIncomingMessage(final Message message) {
        final var properties = message.getMessageProperties();
        log.info("Consumed RabbitMQ message ID: {} from: {} by routing key: {} with body: {}",
            properties.getMessageId(),
            properties.getConsumerQueue(),
            properties.getReceivedRoutingKey(),
            new String(message.getBody()));
    }

    public static void logMessageMarkedForRetry(final MessageProperties properties) {
        log.warn("RabbitMQ message ID: {} from: {} by routing key: {} will be retried!",
            properties.getMessageId(),
            properties.getConsumerQueue(),
            properties.getReceivedRoutingKey());
    }

}
