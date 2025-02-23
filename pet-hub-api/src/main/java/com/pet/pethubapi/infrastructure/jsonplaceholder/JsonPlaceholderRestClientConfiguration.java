package com.pet.pethubapi.infrastructure.jsonplaceholder;

import com.pet.pethublogging.outgoing.RestClientRequestLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
class JsonPlaceholderRestClientConfiguration {

    @Value("${pet.json-placeholder.base-url}")
    private String jsonPlaceholderBaseUrl;

    @Bean
    RestClient jsonPlaceholderRestClient() {
        return RestClient.builder()
            .baseUrl(jsonPlaceholderBaseUrl)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .requestInterceptor(new RestClientRequestLogger())
            .build();
    }

    @Bean
    JsonPlaceholderClient jsonPlaceholderClient(RestClient jsonPlaceholderRestClient) {
        var httpServiceProxyFactory = HttpServiceProxyFactory
            .builderFor(RestClientAdapter.create(jsonPlaceholderRestClient))
            .build();
        return httpServiceProxyFactory.createClient(JsonPlaceholderClient.class);
    }

}
