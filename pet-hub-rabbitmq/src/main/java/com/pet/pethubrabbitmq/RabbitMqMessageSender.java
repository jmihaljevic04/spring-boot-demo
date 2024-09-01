package com.pet.pethubrabbitmq;

import org.springframework.amqp.core.Message;

public interface RabbitMqMessageSender<D> {

    void sendMessageToTopic(String topicExchangeName, String routingKey, D body);

    /**
     * Used as retry mechanism, with headers as indicators that retry has been made.
     */
    void resendMessageToTopic(Message retriedMessage);

    void sendMessageToFanout(String fanoutExchangeName, D body);

}
