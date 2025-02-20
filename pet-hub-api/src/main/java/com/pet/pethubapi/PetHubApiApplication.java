package com.pet.pethubapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableCaching
@EnableRetry
@SpringBootApplication(scanBasePackages = {"com.pet.pethubapi", "com.pet.pethubrabbitmq", "com.pet.pethubsecurity"})
public class PetHubApiApplication {

    protected PetHubApiApplication() {

    }

    public static void main(String[] args) {
        SpringApplication.run(PetHubApiApplication.class, args);
    }

}
