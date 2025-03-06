package com.pet.pethubapi;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Embodies multiple boilerplate annotations when running integration test. All tests which load up whole configuration (security, database) are integration tests.
 * Reason why even mocked tests are integration is due to number of configurations which should be used, utilizing Before/AfterAll callbacks with Mockito and more.
 * It's easier than to create slices and exclude auto-configurations.
 * By default, rabbit-mq container is disabled.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(ShutdownHazelcastCallback.class)
public @interface PetIntegrationTest {

}
