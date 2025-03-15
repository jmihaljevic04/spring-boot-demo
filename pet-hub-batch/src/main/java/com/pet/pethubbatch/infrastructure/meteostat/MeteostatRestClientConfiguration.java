package com.pet.pethubbatch.infrastructure.meteostat;

import com.pet.pethubbatch.domain.BatchApplicationProperties;
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
class MeteostatRestClientConfiguration {

    private final ObservationRegistry observationRegistry;
    private final BatchApplicationProperties applicationProperties;

    @Value("${spring.application.name}")
    private String appName;

    @Bean
    RestClient meteostatRestClient() {
        return RestClient.builder()
            .baseUrl(applicationProperties.getMeteostat().getBaseUrl())
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .defaultHeader(HttpHeaders.CACHE_CONTROL, CacheControl.noCache().getHeaderValue())
            .defaultHeader(HttpHeaders.FROM, appName)
            .observationRegistry(observationRegistry)
            .requestInterceptor(new RestClientRequestLogger())
            .build();
    }

}
