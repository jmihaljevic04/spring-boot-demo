package com.pet.springbootdemo;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(properties = "integration-test.rabbitmq.enabled=true")
@Import(TestcontainersConfiguration.class)
class SpringBootDemoApplicationShould {

    @Test
    void loadContext() {
        Assertions.assertThat(true).isTrue();
    }

}
