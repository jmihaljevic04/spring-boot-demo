package com.pet.pethubbatch.infrastructure.meteostat;

import java.io.IOException;

public class MeteostatIntegrationException extends IOException {

    public MeteostatIntegrationException(String message, Exception exception) {
        super(message, exception);
    }

}
