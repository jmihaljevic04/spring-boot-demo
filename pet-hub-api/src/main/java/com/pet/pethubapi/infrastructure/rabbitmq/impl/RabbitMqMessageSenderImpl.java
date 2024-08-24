package com.pet.pethubapi.infrastructure.rabbitmq.impl;

import com.pet.pethubapi.domain.ApplicationProperties;
import com.pet.pethubapi.infrastructure.rabbitmq.RabbitMqMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMqMessageSenderImpl<D> implements RabbitMqMessageSender<D> {

    private final RabbitTemplate rabbitTemplate;
    private final ApplicationProperties applicationProperties;

    @Override
    public void sendMessageToTopic(D body, String topicExchangeName, String routingKey) {
        if (!applicationProperties.getRabbitmq().isEnabled()) {
            log.warn("Sending message to topic exchange because RabbitMQ integration is disabled!");
            return;
        }

        try {
            final var rabbitMsg = MessageBuilder
                .withBody(body.toString().getBytes(StandardCharsets.UTF_8))
                .setMessageId("msgId")
                .build();
            rabbitTemplate.send(topicExchangeName, routingKey, rabbitMsg);
        } catch (Exception e) {
            log.error("Exception while sending message to topic exchange!", e);
            throw new AmqpException(e);
        }
    }

    @Override
    public void sendMessageToFanout(D body, String fanoutExchangeName) {
        if (!applicationProperties.getRabbitmq().isEnabled()) {
            log.warn("Failed to send message to fanout exchange because RabbitMQ integration is disabled!");
            return;
        }

        try {
            final var rabbitMsg = MessageBuilder
                .withBody(body.toString().getBytes(StandardCharsets.UTF_8))
                .setMessageId("msgId")
                .build();
            rabbitTemplate.send(fanoutExchangeName, "", rabbitMsg);
        } catch (Exception e) {
            log.error("Exception while sending message to fanout exchange!", e);
            throw new AmqpException(e);
        }
    }

}
