package com.pet.pethubbatch.application.weatherstation.dataimport;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidWeatherStationImportActionException extends RuntimeException {

    public InvalidWeatherStationImportActionException(String message, HttpStatus httpStatus) {
        throw new ResponseStatusException(httpStatus, message);
    }

}
