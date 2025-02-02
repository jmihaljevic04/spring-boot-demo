package com.pet.pethubapi.infrastructure.tvmaze;

import com.pet.pethubapi.domain.ApiApplicationProperties;
import com.pet.pethublogging.outgoing.RestClientRequestLogger;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Configuration
class TvMazeRestClientConfiguration {

    private final ObservationRegistry observationRegistry;
    private final ApiApplicationProperties applicationProperties;

    @Value("${spring.application.name}")
    private String appName;

    @Bean
    RestClient tvMazeShowIndexRestClient() {
        return defaultTvMazeRestClient()
            .baseUrl(applicationProperties.getTvMaze().getShowIndex().getBaseUrl())
            .build();
    }

    @Bean
    RestClient tvMazeSearchRestClient() {
        return defaultTvMazeRestClient()
            .baseUrl(applicationProperties.getTvMaze().getSearch().getBaseUrl())
            .build();
    }

    @Bean
    RestClient.Builder defaultTvMazeRestClient() {
        return RestClient.builder()
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.CACHE_CONTROL, CacheControl.noCache().getHeaderValue())
            .defaultHeader(HttpHeaders.FROM, appName)
            .defaultStatusHandler(new TvMazeRestClientErrorHandler(applicationProperties.getTvMaze().getRateLimit().getTimeInterval()))
            .observationRegistry(observationRegistry)
            .requestInterceptor(new RestClientRequestLogger());
    }

}
