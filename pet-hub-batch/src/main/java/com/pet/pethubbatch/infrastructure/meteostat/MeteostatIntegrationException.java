package com.pet.pethubbatch.infrastructure.meteostat;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class MeteostatIntegrationException extends RuntimeException {

    public MeteostatIntegrationException(String message, Exception exception) {
        super(message, exception);
    }

}
