package com.pet.pethubapi.domain;

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
    public static final class JWTProperties {

        private String secretKey;
        private int accessExpiration;
        private int adminAccessExpiration;
        private int refreshExpiration;

    }

}
