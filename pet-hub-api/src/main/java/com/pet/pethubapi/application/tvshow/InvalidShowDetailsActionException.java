package com.pet.pethubapi.application.tvshow;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidShowDetailsActionException extends RuntimeException {

    public InvalidShowDetailsActionException(final String message) {
        super(message);
    }

}
