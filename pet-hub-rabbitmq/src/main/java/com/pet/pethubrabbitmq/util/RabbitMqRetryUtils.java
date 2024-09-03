package com.pet.pethubrabbitmq.util;

import org.apache.commons.collections4.MapUtils;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;

public final class RabbitMqRetryUtils {

    private static final String RETRY_HEADER_NAME = "x-pet-message-retried";

    private RabbitMqRetryUtils() {
        throw new IllegalAccessError("Utility class!");
    }

    public static void checkIfMessageWasRetried(final Message message) {
        final var properties = message.getMessageProperties();
        final var headers = properties.getHeaders();
        if (MapUtils.isNotEmpty(headers) && headers.containsKey(RETRY_HEADER_NAME)) {
            final var msg = String.format("Moved RabbitMQ message ID: %s from: %s by routing key: %s to DLQ! Reached maximum number of retries!",
                properties.getMessageId(),
                properties.getConsumerQueue(),
                properties.getReceivedRoutingKey());
            throw new AmqpRejectAndDontRequeueException(msg);
        }
    }

    public static void markMessageForRetry(final Message message) {
        final var headers = message.getMessageProperties().getHeaders();
        headers.put(RETRY_HEADER_NAME, "true");

        RabbitMqListenerLogger.logMessageMarkedForRetry(message.getMessageProperties());
    }

}
