package com.pet.pethubapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.pet.pethubapi", "com.pet.pethubrabbitmq"})
public class PetHubApiApplication {

    protected PetHubApiApplication() {

    }

    public static void main(String[] args) {
        SpringApplication.run(PetHubApiApplication.class, args);
    }

}
