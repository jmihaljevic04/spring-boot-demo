package com.pet.pethubapi.infrastructure.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * For detailed bindings (and utilizing "#" and "*"), check: <a href="https://www.rabbitmq.com/tutorials/tutorial-five-spring-amqp#topic-exchange">topic exchange binding</a>
 */
@Configuration
public class RabbitMqBindingDefinition {

    private static final int MESSAGE_TTL = (int) Duration.ofHours(1).toMillis();

    public static final String PET_TOPIC_EXCHANGE = "pet-topic-exchange";

    public static final String RETRY_QUEUE = "retry-queue";
    public static final String PARKING_LOT_QUEUE = "parking-lot-queue";

    public static final String RETRY_ROUTING_KEY = ".retry.";
    public static final String DEAD_LETTER_ROUTING_KEY = ".dead-letter.";

    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(PET_TOPIC_EXCHANGE);
    }

    @Bean
    FanoutExchange fanoutExchange() {
        return new FanoutExchange("fanoutExchange");
    }

    @Bean
    Queue queue() {
        return QueueBuilder
            .durable("queue")
            .quorum()
            .ttl(MESSAGE_TTL)
            .deadLetterExchange(PET_TOPIC_EXCHANGE)
            .deadLetterRoutingKey(DEAD_LETTER_ROUTING_KEY + "ttl-expiration")
            .build();
    }

    @Bean
    Queue retryQueue() {
        return QueueBuilder
            .durable(RETRY_QUEUE)
            .quorum()
            .ttl(MESSAGE_TTL)
            .deadLetterExchange(PET_TOPIC_EXCHANGE)
            .deadLetterRoutingKey(DEAD_LETTER_ROUTING_KEY + "retry-ttl-expiration")
            .build();
    }

    /**
     * This queue is not meant to be programmatically consumed. Only for manual manipulation via Admin UI.
     * Reasoning: if original messages couldn't be consumed, there is high probability that dead letters wouldn't be consumed also. Ideally, those would be consumed by third-party and saved in DB.
     */
    // TODO: Prometheus custom metric increment on each message in this queue
    @Bean
    Queue parkingLotQueue() {
        return QueueBuilder
            .durable(PARKING_LOT_QUEUE)
            .quorum()
            .build();
    }

    @Bean
    Binding binding() {
        return BindingBuilder.bind(queue()).to(topicExchange()).with(".queue");
    }

    @Bean
    Binding retryBinding() {
        return BindingBuilder.bind(retryQueue()).to(topicExchange()).with(RETRY_ROUTING_KEY + "#");
    }

    @Bean
    Binding deadLetterBinding() {
        return BindingBuilder.bind(queue()).to(topicExchange()).with(DEAD_LETTER_ROUTING_KEY + "#");
    }

}
