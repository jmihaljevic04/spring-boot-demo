package com.pet.pethubrabbitmq.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "pet.rabbitmq")
@EnableConfigurationProperties(RabbitMqProperties.class)
public class RabbitMqProperties {

    private boolean enabled;

}
