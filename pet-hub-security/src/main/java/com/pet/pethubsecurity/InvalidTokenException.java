package com.pet.pethubsecurity;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public final class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(Exception cause) {
        super("JWT is not valid!", cause);
    }

}
