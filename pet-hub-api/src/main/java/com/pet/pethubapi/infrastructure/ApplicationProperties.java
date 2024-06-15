package com.pet.pethubapi.infrastructure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "pet")
@EnableConfigurationProperties(ApplicationProperties.class)
@Getter
public class ApplicationProperties {

    private final JWTProperties jwt = new JWTProperties();

    @Getter
    @Setter
    private static final class JWTProperties {

        private String secretKey;
        private int expiration;

    }

}
