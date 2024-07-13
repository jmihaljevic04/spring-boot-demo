package com.pet.pethubapi.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class ObjectMapperUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        final var javaTimeModule = new JavaTimeModule();
        mapper.registerModule(javaTimeModule);
    }

    private ObjectMapperUtils() {
        throw new IllegalAccessError();
    }

    public static String toJsonString(final Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new InvalidSerializationException(e);
        }
    }

    private static final class InvalidSerializationException extends RuntimeException {

        public InvalidSerializationException(final Exception e) {
            super(e);
        }

    }

}
