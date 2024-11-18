package com.pet.pethubapi.infrastructure.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j(topic = "audit-logger")
@Component
public class HttpLoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_HEADER_KEY = "X-Request-Id";
    private static final String LOAD_BALANCER_HEADER_KEY = "X-Forwarded-For";
    static final String LOG_ITEM_DELIMITER = ", ";
    private static final String AUTH_URL = "/auth/";
    private static final List<Pattern> SENSITIVE_DATA_PATTERNS = new ArrayList<>(3);
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("(\"password\"\\s*:\\s*\")([^\"]*)(\")");
    private static final Pattern ACCESS_TOKEN_PATTERN = Pattern.compile("(\"accessToken\"\\s*:\\s*\")([^\"]*)(\")");
    private static final Pattern REFRESH_TOKEN_PATTERN = Pattern.compile("(\"refreshToken\"\\s*:\\s*\")([^\"]*)(\")");
    private static final List<String> IGNORED_APIS = new ArrayList<>();

    static {
        SENSITIVE_DATA_PATTERNS.add(PASSWORD_PATTERN);
        SENSITIVE_DATA_PATTERNS.add(ACCESS_TOKEN_PATTERN);
        SENSITIVE_DATA_PATTERNS.add(REFRESH_TOKEN_PATTERN);

        IGNORED_APIS.add("/v3/api-docs");
        IGNORED_APIS.add("/swagger-ui");
        IGNORED_APIS.add("/actuator");
    }

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
        final var shouldLogRequestResponse = IGNORED_APIS.stream().noneMatch(requestUri::contains);

        MDC.put("requestId", requestId);
        MDC.put("authUser", SecurityContextHolder.getContext().getAuthentication().getName());

        try {
            // execute request
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            if (shouldLogRequestResponse) {
                logRequest(httpMethod, requestId, requestUri, source, target, requestWrapper);
                logResponse(responseWrapper, httpMethod, requestId, requestUri, source, target, startTime);
            }

            responseWrapper.copyBodyToResponse();
            MDC.clear();
        }
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

    boolean shouldLogRequestBody(String httpMethod) {
        return !HttpMethod.GET.matches(httpMethod) && !HttpMethod.DELETE.matches(httpMethod);
    }

    /**
     * Trims, normalizes trailing whitespaces into single one and removes new lines.
     */
    String normalizeBody(String originalBody) {
        return StringUtils.normalizeSpace(originalBody.replace("\n", "").replace("\r", ""));
    }

    private boolean isAuthRequest(String requestUri) {
        return requestUri.contains(AUTH_URL);
    }

    private String maskSensitiveData(String body, Pattern pattern) {
        final var matcher = pattern.matcher(body);
        if (matcher.find()) {
            return matcher.replaceAll("$1********$3");
        }

        return body;
    }

    private void logRequest(String httpMethod,
                            String requestId,
                            String requestUri,
                            String source,
                            String target,
                            ContentCachingRequestWrapper requestWrapper) {
        final var requestSb = new StringBuilder();
        requestSb
            .append("HTTP ").append(httpMethod).append(" request :: ")
            .append("Request ID: ").append(requestId)
            .append(LOG_ITEM_DELIMITER).append("Request URI: ").append(requestUri)
            .append(LOG_ITEM_DELIMITER).append("Source: ").append(source)
            .append(LOG_ITEM_DELIMITER).append("Target: ").append(target);

        if (shouldLogRequestBody(httpMethod)) {
            final var requestBody = new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
            if (StringUtils.isBlank(requestBody)) {
                requestSb.append(LOG_ITEM_DELIMITER).append("Request body is empty");
            } else {
                var normalizedRequestBody = normalizeBody(requestBody);

                if (isAuthRequest(requestUri)) {
                    normalizedRequestBody = maskSensitiveData(normalizedRequestBody, PASSWORD_PATTERN);
                }
                requestSb.append(LOG_ITEM_DELIMITER).append("Request body: ").append(normalizedRequestBody);
            }
        }

        log.info(requestSb.toString());
    }

    private void logResponse(ContentCachingResponseWrapper responseWrapper,
                             String httpMethod,
                             String requestId,
                             String requestUri,
                             String source,
                             String target,
                             Instant startTime) {
        final var duration = Duration.between(startTime, Instant.now()).toMillis();
        final var responseStatus = responseWrapper.getStatus();

        final var responseSb = new StringBuilder();
        responseSb
            .append("HTTP ").append(httpMethod).append(" response :: ")
            .append("Request ID: ").append(requestId).append(LOG_ITEM_DELIMITER)
            .append("Request URI: ").append(requestUri).append(LOG_ITEM_DELIMITER)
            .append("Source: ").append(source).append(LOG_ITEM_DELIMITER)
            .append("Target: ").append(target).append(LOG_ITEM_DELIMITER)
            .append("Duration: ").append(duration).append("ms").append(LOG_ITEM_DELIMITER)
            .append("Response status: ").append(responseStatus).append(LOG_ITEM_DELIMITER);

        final var responseBody = new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
        if (StringUtils.isBlank(responseBody)) {
            // this will be also empty when exception occurs
            responseSb.append("Response body is empty");
        } else {
            var normalizedResponseBody = normalizeBody(responseBody);

            if (isAuthRequest(requestUri)) {
                for (var pattern : SENSITIVE_DATA_PATTERNS) {
                    normalizedResponseBody = maskSensitiveData(normalizedResponseBody, pattern);
                }
            }

            responseSb.append("Response body: ").append(normalizedResponseBody);
        }

        log.info(responseSb.toString());
    }

}
