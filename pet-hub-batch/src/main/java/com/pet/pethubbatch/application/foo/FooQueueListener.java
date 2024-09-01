package com.pet.pethubbatch.application.foo;

import com.pet.pethubbatch.infrastructure.logging.RabbitMQListenerLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(value = "pet.rabbitmq.enabled", havingValue = "true")
public class FooQueueListener {

    @RabbitListener(id = "foo-queue-listener", queues = "foo-queue", containerFactory = "rabbitListenerContainerFactory")
    public void consumeMessage(Message message) {
        RabbitMQListenerLogger.logIncomingMessage(message);
        try {
            // processing message
            throw new RuntimeException();
        } catch (Exception e) {
            // send message to DLQ
            throw new AmqpRejectAndDontRequeueException("Moved message from foo-queue to DLQ!", e);
        }
    }

}
