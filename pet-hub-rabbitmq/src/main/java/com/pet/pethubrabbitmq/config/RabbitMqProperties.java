package com.pet.pethubrabbitmq.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "pet.rabbitmq")
@EnableConfigurationProperties(RabbitMqProperties.class)
@PropertySource("classpath:rabbitmq.properties")
public class RabbitMqProperties {

    private boolean enabled;

}
