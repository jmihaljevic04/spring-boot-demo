package com.pet.pethubsecurity.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "pet.jwt")
@EnableConfigurationProperties(JwtProperties.class)
@PropertySource("classpath:security.properties")
public class JwtProperties {

    private String secretKey;
    private int accessExpiration;
    private int adminAccessExpiration;
    private int refreshExpiration;

}
