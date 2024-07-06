package com.pet.pethubapi.application.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class ObjectMapperUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
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
