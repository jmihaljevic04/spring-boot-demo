package com.pet.pethubbatch.infrastructure.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;

@Slf4j
public final class RabbitMQListenerLogger {

    private RabbitMQListenerLogger() {
        throw new IllegalAccessError("Utility class");
    }

    public static void logIncomingMessage(final Message message) {
        final var props = message.getMessageProperties();
        log.info("Consumed message from: {} by routing key: {} with message ID: {} and body: {}",
            props.getConsumerQueue(),
            props.getReceivedRoutingKey(),
            props.getMessageId(),
            new String(message.getBody()));
    }

}
