package com.pet.pethubrabbitmq;

public interface RabbitMqMessageSender<D> {

    void sendMessageToTopic(D body, String topicExchangeName, String routingKey);

    void sendMessageToFanout(D body, String fanoutExchangeName);

}
