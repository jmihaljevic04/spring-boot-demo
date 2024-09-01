package com.pet.pethubrabbitmq;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "pet.rabbitmq")
@EnableConfigurationProperties(RabbitMqProperties.class)
@Getter
@Setter
public class RabbitMqProperties {

    private boolean enabled;

}
