package com.pet.pethubapi;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(properties = {"pet.integration-test.rabbitmq-container.enabled=true", "pet.integration-test.mongodb-container.enabled=true"})
@Import(TestcontainersConfiguration.class)
class PetHubApiApplicationShould {

    @Test
    void loadContext() {
        Assertions.assertThat(true).isTrue();
    }

}
