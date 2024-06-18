package com.pet.pethubapi.infrastructure.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(Exception cause) {
        super("Authentication token is not valid!", cause);
    }

}
