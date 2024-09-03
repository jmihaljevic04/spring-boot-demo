package com.pet.pethubbatch.application.foo;

import com.pet.pethubrabbitmq.config.RabbitMqBindingDefinition;
import com.pet.pethubrabbitmq.sender.RabbitMqMessageSender;
import com.pet.pethubrabbitmq.util.RabbitMqListenerLogger;
import com.pet.pethubrabbitmq.util.RabbitMqRetryUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(value = "pet.rabbitmq.enabled", havingValue = "true")
@RequiredArgsConstructor
public class FooQueueListener {

    private final RabbitMqMessageSender<String> messageSender;

    @RabbitListener(id = "foo-queue-listener-dlq", queues = RabbitMqBindingDefinition.FOO_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void consumeMessageWithDlq(final Message message) {
        RabbitMqListenerLogger.logIncomingMessage(message);
        try {
            // processing message
            throw new RuntimeException();
        } catch (Exception e) {
            // send message to DLQ
            throw new AmqpRejectAndDontRequeueException("Moved message from foo-queue to DLQ with error!", e);
        }
    }

    @RabbitListener(id = "foo-queue-listener-retry", queues = RabbitMqBindingDefinition.FOO_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void consumeMessageWithRetry(final Message message) {
        RabbitMqListenerLogger.logIncomingMessage(message);
        RabbitMqRetryUtils.checkIfMessageWasRetried(message);

        try {
            // processing message
            throw new RuntimeException();
        } catch (Exception e) {
            // retry message
            RabbitMqRetryUtils.markMessageForRetry(message);
            messageSender.resendMessageToTopic(message);
        }
    }

}
