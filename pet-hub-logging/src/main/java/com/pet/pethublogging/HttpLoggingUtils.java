package com.pet.pethublogging;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;

public final class HttpLoggingUtils {

    private HttpLoggingUtils() {

    }

    public static final String LOG_ITEM_DELIMITER = ", ";

    public static boolean shouldLogRequestBody(String httpMethod, String contentType) {
        try {
            return shouldLogRequestBody(httpMethod, MediaType.parseMediaType(contentType));
        } catch (InvalidMediaTypeException e) {
            return false;
        }
    }

    public static boolean shouldLogRequestBody(String httpMethod, MediaType mediaType) {
        final var isValidHttpMethod = !HttpMethod.GET.matches(httpMethod) && !HttpMethod.DELETE.matches(httpMethod);
        final var isValidContentType = isContentTypeForLogging(mediaType);

        return isValidHttpMethod && isValidContentType;
    }

    public static boolean shouldLogResponseBody(String contentType) {
        try {
            return shouldLogResponseBody(MediaType.parseMediaType(contentType));
        } catch (InvalidMediaTypeException e) {
            return false;
        }
    }

    public static boolean shouldLogResponseBody(MediaType mediaType) {
        return isContentTypeForLogging(mediaType);
    }

    private static boolean isContentTypeForLogging(MediaType mediaType) {
        return MediaType.APPLICATION_JSON.isCompatibleWith(mediaType) || MediaType.TEXT_PLAIN.isCompatibleWith(mediaType);
    }

    /**
     * Trims, normalizes trailing whitespaces into single one and removes new lines.
     */
    public static String normalizeBody(String originalBody) {
        return StringUtils.normalizeSpace(originalBody.replace("\n", "").replace("\r", ""));
    }

}
