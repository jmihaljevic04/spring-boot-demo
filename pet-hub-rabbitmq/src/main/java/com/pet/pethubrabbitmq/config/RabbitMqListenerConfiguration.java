package com.pet.pethubrabbitmq.config;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
class RabbitMqListenerConfiguration {

    /**
     * Configuration does not implement Spring retry policy due to the fact it has blocking effect on consumer (each retry keeps consumer instance blocked until all retries are exhausted).
     * Having concurrent consumers could mitigate that issue, but still is blocking.
     * Implemented solution is using retry queue combined with death-letter exchange and queue (acting as parking lot for dead messages), which is not blocking consumer instance.
     */
    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(final ConnectionFactory connectionFactory) {
        final var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setPrefetchCount(0); // one consumer will not prefetch more than one message at a time (default 250 prefetched messages in memory; configured in favor of concurrent consumers)
        factory.setConcurrentConsumers(3); // defines number of concurrent consumers (scenario where producer sends multiple messages at once to same consumer; avoid storing queued messages in memory)
        factory.setMaxConcurrentConsumers(5);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

}
