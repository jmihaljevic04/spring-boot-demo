package com.pet.pethubapi.infrastructure.tvmaze;

import com.pet.pethubapi.domain.tvshow.TvShowDTO;
import com.pet.pethubapi.domain.tvshow.TvShowDetailsRepository;
import com.pet.pethubapi.domain.tvshow.TvShowSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryException;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
class TvMazeShowDetailsRepositoryImpl implements TvShowDetailsRepository {

    private final RestClient tvMazeShowIndexRestClient;
    private final RestClient tvMazeSearchRestClient;

    @Override
    public List<TvShowDTO> getAllShows(int pageNumber) {
        final var responseBody = tvMazeShowIndexRestClient.get()
            .uri(uriBuilder -> uriBuilder.queryParam("page", pageNumber).build())
            .retrieve()
            .body(TvShowDTO[].class);

        if (responseBody == null) {
            return Collections.emptyList();
        }

        return Arrays.asList(responseBody);
    }

    @Override
    public List<TvShowSearchResponse> getShowDetailsByName(String name) {
        final var responseBody = tvMazeSearchRestClient.get()
            .uri(uriBuilder -> uriBuilder.queryParam("q", name).build())
            .retrieve()
            .body(TvShowSearchResponse[].class);

        if (responseBody == null) {
            return Collections.emptyList();
        }

        return Arrays.asList(responseBody);
    }

    @Override
    public Optional<TvShowDTO> getShowDetailsById(Long id) {
        final TvShowDTO responseBody = executeRetryableGetShowDetailsByIdRequest(id);

        if (responseBody == null) {
            return Optional.empty();
        }

        return Optional.of(responseBody);
    }

    @SneakyThrows
    @Override
    public Optional<TvShowDTO> logFailedRequest(Exception apiException, Long originalInput) {
        log.error("Unexpected error while fetching TV Show details from TV Maze with ID: {}!", originalInput, apiException);
        throw switch (apiException) {
            case RetryException e -> e.getCause();
            default -> apiException;
        };
    }

    private TvShowDTO executeRetryableGetShowDetailsByIdRequest(Long id) {
        final TvShowDTO responseBody;
        try {
            responseBody = tvMazeShowIndexRestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/" + id).build())
                .retrieve()
                .body(TvShowDTO.class);
        } catch (RestClientException retryableException) {
            throw new RetryException(retryableException.getMessage(), retryableException);
        } catch (Exception exception) {
            logFailedRequest(exception, id);
            throw exception;
        }

        return responseBody;
    }

}
