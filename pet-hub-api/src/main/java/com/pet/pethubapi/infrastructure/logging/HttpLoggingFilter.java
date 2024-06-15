package com.pet.pethubapi.infrastructure.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
public class HttpLoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_HEADER_KEY = "X-Request-ID";
    private static final String LOAD_BALANCER_HEADER_KEY = "X-Forwaded-For";
    private static final String LOG_ITEM_DELIMITER = ", ";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final var requestWrapper = new ContentCachingRequestWrapper(request);
        final var responseWrapper = new ContentCachingResponseWrapper(response);
        final var requestId = getUniqueIdentifier(requestWrapper);
        final var httpMethod = request.getMethod();
        final var requestUri = getRequestUri(requestWrapper);
        final var source = getSource(requestWrapper);
        final var target = request.getServerName();

        final var startTime = Instant.now();
        filterChain.doFilter(requestWrapper, responseWrapper);
        final var duration = Duration.between(startTime, Instant.now()).toMillis();

        final var requestSb = new StringBuilder();
        requestSb
            .append("HTTP ").append(httpMethod).append(" request :: ")
            .append("Request ID: ").append(requestId).append(LOG_ITEM_DELIMITER)
            .append("Request URI: ").append(requestUri).append(LOG_ITEM_DELIMITER)
            .append("Source: ").append(source).append(LOG_ITEM_DELIMITER)
            .append("Target: ").append(target).append(LOG_ITEM_DELIMITER);

        if (shouldLogRequestBody(httpMethod)) {
            final var requestBody = new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
            requestSb.append("Request body: ").append(requestBody);
        }

        final var responseStatus = response.getStatus();
        final var responseBody = new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
        final var responseSb = new StringBuilder();
        responseSb
            .append("HTTP ").append(httpMethod).append(" response :: ")
            .append("Request ID: ").append(requestId).append(LOG_ITEM_DELIMITER)
            .append("Request URI: ").append(requestUri).append(LOG_ITEM_DELIMITER)
            .append("Source: ").append(source).append(LOG_ITEM_DELIMITER)
            .append("Target: ").append(target).append(LOG_ITEM_DELIMITER)
            .append("Response status: ").append(responseStatus).append(LOG_ITEM_DELIMITER)
            .append("Response body: ").append(responseBody).append(LOG_ITEM_DELIMITER)
            .append("Duration: ").append(duration).append("ms");

        log.info(requestSb.toString());
        log.info(responseSb.toString());

        responseWrapper.copyBodyToResponse();
    }

    private String getUniqueIdentifier(ContentCachingRequestWrapper request) {
        final var requestIdFromHeader = request.getHeader(REQUEST_ID_HEADER_KEY);
        return StringUtils.isEmpty(requestIdFromHeader) ? UUID.randomUUID().toString() : requestIdFromHeader;
    }

    private String getRequestUri(ContentCachingRequestWrapper request) {
        final var base = request.getRequestURI();
        final var params = request.getParameterMap();

        if (CollectionUtils.isEmpty(params)) {
            return base;
        }

        final var sb = new StringBuilder();
        sb.append("?");
        params.forEach((key, value) -> sb.append(key).append("=").append(value[0]).append("&"));
        sb.deleteCharAt(sb.length() - 1);

        return base.concat(sb.toString());
    }

    private String getSource(ContentCachingRequestWrapper request) {
        final var lbHeader = request.getHeader(LOAD_BALANCER_HEADER_KEY);
        return StringUtils.isEmpty(lbHeader) ? request.getRemoteAddr() : lbHeader;
    }

    private boolean shouldLogRequestBody(String httpMethod) {
        return !HttpMethod.GET.matches(httpMethod) && !HttpMethod.DELETE.matches(httpMethod);
    }

}
