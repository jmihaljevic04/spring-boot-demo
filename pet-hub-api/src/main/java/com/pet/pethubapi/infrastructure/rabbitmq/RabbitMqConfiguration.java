package com.pet.pethubapi.infrastructure.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.support.RetryTemplateBuilder;

import java.time.Duration;

@Slf4j
@Configuration
@EnableRabbit
public class RabbitMqConfiguration {

    /**
     * Configuration does not implement Spring retry policy due to the fact it has blocking effect on consumer (each retry keeps consumer instance blocked until all retries are exhausted).
     * Having concurrent consumers could mitigate that issue, but still is blocking. Implemented solution is using retry queue combined with death-letter exchange and parking-lot queue, which is not blocking consumer instance.
     */
    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(final ConnectionFactory connectionFactory) {
        final var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(rabbitJsonMessageConverter());
        factory.setPrefetchCount(0); // one consumer will not prefetch more than one message at a time (default 250 prefetched messages in memory; configured in favor of concurrent consumers)
        factory.setConcurrentConsumers(3); // defines number of concurrent consumers (scenario where producer sends multiple messages at once to same consumer; avoid storing queued messages in memory)
        factory.setMaxConcurrentConsumers(5);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

    @Bean
    RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final var template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(rabbitJsonMessageConverter());
        template.setObservationEnabled(true);

        final var retryPolicy = new RetryTemplateBuilder()
            .maxAttempts(3)
            .fixedBackoff(Duration.ofSeconds(1))
            .withListener(new RetryListener() { // logs exception which triggered retry on sending message via template
                @Override
                public <T, E extends Throwable> void onError(RetryContext context,
                                                             RetryCallback<T, E> callback,
                                                             Throwable throwable) {
                    log.error("Unexpected error while sending RabbitMQ message!", throwable);
                    RetryListener.super.onError(context, callback, throwable);
                }
            })
            .retryOn(AmqpException.class)
            .traversingCauses()
            .build();
        template.setRetryTemplate(retryPolicy);
        return template;
    }

    @Bean
    MessageConverter rabbitJsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
