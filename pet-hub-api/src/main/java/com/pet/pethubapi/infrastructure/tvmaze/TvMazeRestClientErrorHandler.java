package com.pet.pethubapi.infrastructure.tvmaze;

import com.pet.pethubapi.application.ObjectMapperUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Component
public class TvMazeRestClientErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        final var errorResponseBody = ObjectMapperUtils.OBJECT_MAPPER.readValue(response.getBody(), TvMazeErrorResponse.class);
        throw new TvMazeIntegrationException("Error when calling TvMaze API with HTTP status code: " + response.getStatusCode().value() + " and message: " + errorResponseBody.getMessage() + "!");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    private static final class TvMazeIntegrationException extends RuntimeException {

        public TvMazeIntegrationException(final String message) {
            super(message);
        }

    }

    @Getter
    @Setter
    private static final class TvMazeErrorResponse {

        private String message;
        private int code;

    }

}
