package com.pet.pethubapi.infrastructure.rabbitmq;

public interface RabbitMqMessageSender<D> {

    void sendMessageToTopic(D body, String topicExchangeName, String routingKey);

    void sendMessageToFanout(D body, String fanoutExchangeName);

}
