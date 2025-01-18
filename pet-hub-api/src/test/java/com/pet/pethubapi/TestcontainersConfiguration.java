package com.pet.pethubapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;

// https://github.com/spring-io/start.spring.io/issues/1247
@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:16.3-alpine").withReuse(true);
    }

    @Bean
    @ConditionalOnProperty(name = "pet.integration-test.rabbitmq-container.enabled", havingValue = "true")
    @ServiceConnection
    RabbitMQContainer rabbitContainer() {
        return new RabbitMQContainer("rabbitmq:3.13.6-alpine").withReuse(true);
    }

    @Bean
    //@ConditionalOnProperty(name = "pet.integration-test.mongodb-container.enabled", havingValue = "true") => check application-default.properties
    @ServiceConnection
    MongoDBContainer mongoDbContainer() {
        return new MongoDBContainer("mongo:8.0-rc-noble").withReuse(true);
    }

    public static void main(String[] args) {
        SpringApplication.from(PetHubApiApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
