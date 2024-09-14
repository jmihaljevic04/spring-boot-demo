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

    private static final RestClient.Builder TV_MAZE_REST_CLIEN_BUILDER = RestClient.builder()
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.CACHE_CONTROL, CacheControl.maxAge(Duration.ZERO).getHeaderValue())
        .defaultStatusHandler(new TvMazeRestClientErrorHandler())
        .requestInterceptor(new RestClientRequestLogger())
        .observationRegistry(ObservationRegistry.create());  // for Micrometer observability: https://docs.spring.io/spring-framework/reference/integration/observability.html#observability.http-client.restclient

    private final ApiApplicationProperties applicationProperties;
    @Value("${spring.application.name}")
    private String appName;

    @Bean
    RestClient tvMazeShowIndexRestClient() {
        return TV_MAZE_REST_CLIEN_BUILDER
            .baseUrl(applicationProperties.getTvMaze().getShowIndex().getBaseUrl())
            .defaultHeader(HttpHeaders.FROM, appName)
            .build();
    }

    @Bean
    RestClient tvMazeSearchRestClient() {
        return TV_MAZE_REST_CLIEN_BUILDER
            .baseUrl(applicationProperties.getTvMaze().getSearch().getBaseUrl())
            .defaultHeader(HttpHeaders.FROM, appName)
            .build();
    }

}
