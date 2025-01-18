package com.pet.pethubapi.infrastructure.logging;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static com.pet.pethubapi.infrastructure.logging.HttpLoggingFilter.LOG_ITEM_DELIMITER;

@Slf4j(topic = "audit-logger")
@Component
public class RestClientRequestLogger implements ClientHttpRequestInterceptor {

    @Override
    public @NonNull ClientHttpResponse intercept(@NonNull HttpRequest request, @NonNull byte[] body, @NonNull ClientHttpRequestExecution execution)
        throws IOException {
        final var httpMethod = request.getMethod();
        final var requestUri = request.getURI().toString();
        final var requestId = UUID.randomUUID().toString();
        final var startTime = Instant.now();

        logRequest(body, requestId, requestUri, httpMethod);

        final ClientHttpResponse response = execution.execute(request, body);

        final byte[] responseBody = response.getBody().readAllBytes();
        logResponse(response.getStatusCode().value(), responseBody, requestId, requestUri, httpMethod, startTime);

        return new BufferingClientHttpResponseWrapper(response, responseBody);
    }

    private void logRequest(byte[] body, String requestId, String requestUri, HttpMethod httpMethod) {
        final var requestSb = new StringBuilder();
        requestSb
            .append("EXTERNAL HTTP ").append(httpMethod.name()).append(" request :: ")
            .append("Request ID: ").append(requestId).append(LOG_ITEM_DELIMITER)
            .append("Request URI: ").append(requestUri);

        if (HttpLoggingFilter.shouldLogRequestBody(httpMethod.name())) {
            final var requestBody = new String(body, StandardCharsets.UTF_8);
            if (StringUtils.isEmpty(requestBody)) {
                requestSb.append(LOG_ITEM_DELIMITER).append("Request body is empty");
            } else {
                requestSb.append(LOG_ITEM_DELIMITER).append("Request body: ").append(HttpLoggingFilter.normalizeBody(requestBody));
            }
        }

        log.info(requestSb.toString());
    }

    private static void logResponse(int responseStatus,
                                    byte[] responseBody,
                                    String requestId,
                                    String requestUri,
                                    HttpMethod httpMethod,
                                    Instant startTime) {
        final var duration = Duration.between(startTime, Instant.now()).toMillis();
        final var responseSb = new StringBuilder();
        responseSb
            .append("EXTERNAL HTTP ").append(httpMethod.name()).append(" response :: ")
            .append("Request ID: ").append(requestId).append(LOG_ITEM_DELIMITER)
            .append("Request URI: ").append(requestUri).append(LOG_ITEM_DELIMITER)
            .append("Duration: ").append(duration).append("ms").append(LOG_ITEM_DELIMITER)
            .append("Response status: ").append(responseStatus).append(LOG_ITEM_DELIMITER);

        if (ArrayUtils.isEmpty(responseBody)) {
            responseSb.append("Response body is empty");
        } else {
            final var stringifyBody = new String(responseBody, StandardCharsets.UTF_8);
            final var normalizedResponseBody = HttpLoggingFilter.normalizeBody(stringifyBody);
            responseSb.append("Response body: ").append(normalizedResponseBody);
        }

        log.info(responseSb.toString());
    }

    private record BufferingClientHttpResponseWrapper(ClientHttpResponse response, byte[] body) implements ClientHttpResponse {

        @Override
        public @NonNull InputStream getBody() {
            return new ByteArrayInputStream(body);
        }

        @Override
        public @NonNull HttpStatusCode getStatusCode() throws IOException {
            return response.getStatusCode();
        }

        @Override
        public @NonNull String getStatusText() throws IOException {
            return response.getStatusText();
        }

        @Override
        public void close() {
            response.close();
        }

        @Override
        public @NonNull HttpHeaders getHeaders() {
            return response.getHeaders();
        }

    }

}
