package com.pet.pethubbatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;

// https://github.com/spring-io/start.spring.io/issues/1247
@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:17.4-alpine")
            .withDatabaseName("pet_hub_batch_test")
            .withReuse(true);
    }

    @Bean
    @ConditionalOnProperty(name = "pet.integration-test.rabbitmq-container.enabled", havingValue = "true")
    @ServiceConnection
    RabbitMQContainer rabbitContainer() {
        return new RabbitMQContainer("rabbitmq:3.13.6-alpine").withReuse(true);
    }

    public static void main(String[] args) {
        SpringApplication.from(PetHubBatchApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
