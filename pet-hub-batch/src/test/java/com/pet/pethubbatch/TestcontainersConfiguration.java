package com.pet.pethubbatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

// https://github.com/spring-io/start.spring.io/issues/1247
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:16.3-alpine")).withReuse(true);
    }

    @Bean
    @ConditionalOnProperty(name = "pet.integration-test.rabbitmq-container.enabled", havingValue = "true")
    @ServiceConnection
    RabbitMQContainer rabbitContainer() {
        return new RabbitMQContainer(DockerImageName.parse("rabbitmq:3.13-alpine")).withReuse(true);
    }

    public static void main(String[] args) {
        SpringApplication.from(PetHubBatchApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
