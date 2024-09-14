package com.pet.pethubapi.infrastructure.logging;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j(topic = "audit-logger")
@Component
public class RestClientRequestLogger implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request,
                                        byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        final var sb = new StringBuilder();
        sb
            .append("EXTERNAL HTTP ").append(request.getMethod().name()).append(" request :: ")
            .append("Request URI: ").append(request.getURI());

        if (HttpLoggingFilter.shouldLogRequestBody(request.getMethod().name())) {
            final var requestBody = new String(body, StandardCharsets.UTF_8);
            if (StringUtils.isEmpty(requestBody)) {
                sb.append(" with empty request body");
            } else {
                sb.append(" with request body: ").append(HttpLoggingFilter.normalizeBody(requestBody));
            }
        }

        log.info(sb.toString());
        return execution.execute(request, body);
    }

}
