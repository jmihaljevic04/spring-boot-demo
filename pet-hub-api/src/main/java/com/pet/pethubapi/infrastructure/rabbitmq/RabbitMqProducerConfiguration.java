package com.pet.pethubapi.infrastructure.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.support.RetryTemplateBuilder;

import java.time.Duration;

@Slf4j
@Configuration
public class RabbitMqProducerConfiguration {

    @Bean
    RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final var template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
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

}
