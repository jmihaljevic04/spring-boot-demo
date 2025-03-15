package com.pet.pethubapi.infrastructure.tvmaze;

import com.pet.pethubapi.application.ObjectMapperUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@RequiredArgsConstructor
class TvMazeRestClientErrorHandler implements ResponseErrorHandler {

    private final Integer tvMazeRateLimit;

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (HttpStatus.TOO_MANY_REQUESTS.equals(response.getStatusCode())) {
            // this is valid for requests which aren't retryable; those who are retryable will handle this gracefully
            // theoretically shouldn't happen due to implemented request overflow validation
            throw new TvMazeIntegrationException("TVMaze API responded with too many requests. Requests for next " + tvMazeRateLimit + " seconds will be ignored!");
        }

        final var errorResponseBody = ObjectMapperUtils.readJson(response.getBody(), TvMazeErrorResponse.class);
        throw new TvMazeIntegrationException("Error when calling TvMaze API with HTTP status code: " + response.getStatusCode().value() + " and message: '" + errorResponseBody.getMessage() + "'!");
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class TvMazeErrorResponse {

        private int code;
        private String message;

    }

}
