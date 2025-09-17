package com.pet.pethubsecurity.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public final class InvalidAuthenticationException extends RuntimeException {

    public InvalidAuthenticationException(final String message) {
        super(message);
    }

}
