package com.pet.pethubbatch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;

@Slf4j
@EnableScheduling
@EnableCaching
@SpringBootApplication
public class PetHubBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetHubBatchApplication.class, args);
    }

    @EventListener(ApplicationStartedEvent.class)
    void attachSwaggerLinkToStartupLog(ApplicationStartedEvent event) throws Exception {
        final var host = InetAddress.getLocalHost().getHostAddress();
        final var port = event.getApplicationContext().getEnvironment().getProperty("server.port", String.class);
        final var contextPart = event.getApplicationContext().getEnvironment().getProperty("server.servlet.context-path", String.class);
        final var swagger = event.getApplicationContext().getEnvironment().getProperty("springdoc.swagger-ui.path", String.class);
        log.info("\n-------------\nSwaggerUI available at: {}:{}{}{}\n-------------", host, port, contextPart, swagger);
    }

}
