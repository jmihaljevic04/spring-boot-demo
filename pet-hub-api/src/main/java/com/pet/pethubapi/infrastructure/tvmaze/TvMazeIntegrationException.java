package com.pet.pethubapi.infrastructure.tvmaze;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public final class TvMazeIntegrationException extends RuntimeException {

    public TvMazeIntegrationException(final String message) {
        super(message);
    }

}
