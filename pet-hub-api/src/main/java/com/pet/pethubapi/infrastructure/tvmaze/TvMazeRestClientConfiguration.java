package com.pet.pethubapi.infrastructure.tvmaze;

import com.pet.pethubapi.domain.ApiApplicationProperties;
import com.pet.pethubapi.infrastructure.logging.RestClientRequestLogger;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@RequiredArgsConstructor
@Configuration
class TvMazeRestClientConfiguration {

    private final ObservationRegistry observationRegistry;
    private final ApiApplicationProperties applicationProperties;

    @Value("${spring.application.name}")
    private String appName;

    @Bean
    RestClient tvMazeShowIndexRestClient() {
        return getDefaultRestClientBuilder()
            .baseUrl(applicationProperties.getTvMaze().getShowIndex().getBaseUrl())
            .build();
    }

    @Bean
    RestClient tvMazeSearchRestClient() {
        return getDefaultRestClientBuilder()
            .baseUrl(applicationProperties.getTvMaze().getSearch().getBaseUrl())
            .build();
    }

    private RestClient.Builder getDefaultRestClientBuilder() {
        return RestClient.builder()
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.CACHE_CONTROL, CacheControl.maxAge(Duration.ZERO).getHeaderValue())
            .defaultStatusHandler(new TvMazeRestClientErrorHandler(applicationProperties.getTvMaze().getRateLimit().getTimeInterval()))
            .observationRegistry(observationRegistry)
            .defaultHeader(HttpHeaders.FROM, appName)
            .requestInterceptor(new RestClientRequestLogger());
    }

}
