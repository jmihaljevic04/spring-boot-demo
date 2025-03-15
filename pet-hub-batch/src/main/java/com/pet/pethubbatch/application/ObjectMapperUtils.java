package com.pet.pethubbatch.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class ObjectMapperUtils {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        final var javaTimeModule = new JavaTimeModule();
        OBJECT_MAPPER.registerModule(javaTimeModule);
    }

    private ObjectMapperUtils() {
        throw new IllegalAccessError("Utility class!");
    }

    public static String writeJson(final Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new InvalidJsonProcessingException(e);
        }
    }

    public static <T> T readJson(final String jsonString, final Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(jsonString, clazz);
        } catch (JsonProcessingException e) {
            throw new InvalidJsonProcessingException(e);
        }
    }

    private static final class InvalidJsonProcessingException extends RuntimeException {

        private InvalidJsonProcessingException(final Exception e) {
            super(e);
        }

    }

}
