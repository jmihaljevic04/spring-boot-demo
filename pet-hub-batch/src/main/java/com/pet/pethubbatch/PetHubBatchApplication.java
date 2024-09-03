package com.pet.pethubbatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.pet.pethubbatch", "com.pet.pethubrabbitmq", "com.pet.pethubsecurity"})
public class PetHubBatchApplication {

    protected PetHubBatchApplication() {

    }

    public static void main(String[] args) {
        SpringApplication.run(PetHubBatchApplication.class, args);
    }

}
