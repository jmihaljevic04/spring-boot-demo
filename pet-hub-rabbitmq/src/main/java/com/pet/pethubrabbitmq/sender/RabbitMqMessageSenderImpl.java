package com.pet.pethubrabbitmq.sender;

import com.pet.pethubrabbitmq.config.RabbitMqProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
class RabbitMqMessageSenderImpl<D> implements RabbitMqMessageSender<D> {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqProperties rabbitMqProperties;

    @Override
    public void sendMessageToTopic(String topicExchangeName, String routingKey, D body) {
        if (!rabbitMqProperties.isEnabled()) {
            log.warn("Sending message to topic exchange because RabbitMQ integration is disabled!");
            return;
        }

        try {
            final var rabbitMsg = MessageBuilder
                .withBody(body.toString().getBytes(StandardCharsets.UTF_8))
                .setMessageId("traceId")
                .build();
            rabbitTemplate.send(topicExchangeName, routingKey, rabbitMsg);
        } catch (Exception e) {
            log.error("Exception while sending message to topic exchange!", e);
            throw new AmqpException(e);
        }
    }

    @Override
    public void resendMessageToTopic(Message retriedMessage) {
        final var props = retriedMessage.getMessageProperties();
        rabbitTemplate.send(props.getReceivedExchange(), props.getReceivedRoutingKey(), retriedMessage);
    }

    @Override
    public void sendMessageToFanout(String fanoutExchangeName, D body) {
        if (!rabbitMqProperties.isEnabled()) {
            log.warn("Failed to send message to fanout exchange because RabbitMQ integration is disabled!");
            return;
        }

        try {
            final var rabbitMsg = MessageBuilder
                .withBody(body.toString().getBytes(StandardCharsets.UTF_8))
                .setMessageId("traceId")
                .build();
            rabbitTemplate.send(fanoutExchangeName, "", rabbitMsg);
        } catch (Exception e) {
            log.error("Exception while sending message to fanout exchange!", e);
            throw new AmqpException(e);
        }
    }

}
