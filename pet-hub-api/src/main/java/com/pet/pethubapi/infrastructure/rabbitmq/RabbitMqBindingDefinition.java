package com.pet.pethubapi.infrastructure.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
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

    private static final String DEAD_LETTER_EXCHANGE = "pet-dead-letter-exchange";
    private static final String DEAD_LETTER_ROUTING_KEY = "dead-letter-rk";

    public static final String PET_TOPIC_EXCHANGE = "pet-topic-exchange";
    public static final String PET_FANOUT_EXCHANGE = "pet-fanout-exchange";

    public static final String FOO_QUEUE = "foo-queue";
    public static final String RETRY_QUEUE = "retry-queue";

    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(PET_TOPIC_EXCHANGE);
    }

    @Bean
    FanoutExchange fanoutExchange() {
        return new FanoutExchange(PET_FANOUT_EXCHANGE);
    }

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    @Bean
    Queue fooQueue() {
        return QueueBuilder
            .durable(FOO_QUEUE)
            .quorum()
            .ttl(MESSAGE_TTL)
            .deadLetterExchange(DEAD_LETTER_EXCHANGE)
            .deadLetterRoutingKey(DEAD_LETTER_ROUTING_KEY)
            .build();
    }

    @Bean
    Queue retryQueue() {
        return QueueBuilder
            .durable(RETRY_QUEUE)
            .quorum()
            .ttl(MESSAGE_TTL)
            .deadLetterExchange(DEAD_LETTER_EXCHANGE)
            .deadLetterRoutingKey(DEAD_LETTER_ROUTING_KEY)
            .build();
    }

    /**
     * This queue is not meant to be programmatically consumed. Only for manual manipulation via Admin UI.
     * Reasoning: if original messages couldn't be consumed, there is high probability that dead letters wouldn't be consumed also. Ideally, those would be consumed by third-party and saved in DB.
     */
    @Bean
    Queue deadLetterQueue() {
        return QueueBuilder
            .durable("dead-letter-queue")
            .quorum()
            .build();
    }

    @Bean
    Binding fooQueueTopicbinding() {
        return BindingBuilder.bind(fooQueue()).to(topicExchange()).with(FOO_QUEUE + ".#");
    }

    @Bean
    Binding retryBinding() {
        return BindingBuilder.bind(retryQueue()).to(topicExchange()).with(RETRY_QUEUE + ".#");
    }

    @Bean
    Binding deadLetterQueueBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(DEAD_LETTER_ROUTING_KEY);
    }

}
