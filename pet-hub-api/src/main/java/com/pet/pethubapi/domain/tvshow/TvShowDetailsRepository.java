package com.pet.pethubapi.domain.tvshow;

import org.springframework.retry.RetryException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

import java.util.List;
import java.util.Optional;

public interface TvShowDetailsRepository {

    List<TvShowDTO> getAllShows(int pageNumber);

    /**
     * Method invocation has been cached by input param (show name) as cache key.
     */
    List<TvShowSearchResponse> getShowDetailsByName(String name);

    /**
     * Retryable method, retrying API call on all 4XX & 5XX status codes (including timeout exceptions).
     * Backoff configuration defined in separate class.
     *
     * @see TvShowDetailsRepositoryRetryConfiguration
     */
    @Retryable(retryFor = RetryException.class,
        maxAttempts = 4,
        backoff = @Backoff(delayExpression = "#{@backoffDelay}", multiplierExpression = "#{@backoffDelayMultiplier}", maxDelayExpression = "#{@backoffDelayMaxValue}"),
        label = "tv-show.get-by-id")
    Optional<TvShowDTO> getShowDetailsById(Long id);

    /**
     * Recovery method when all retries are exhausted. Accepts exception and input from original (first) request.
     * Method signature must match to be applied as recoverable method (same return value, exception and original input).
     * Recovery method must be declared within same interface to be applied.
     *
     * @return always throws exception
     */
    @SuppressWarnings("unused")
    @Recover
    Optional<TvShowDTO> logFailedRequest(Exception apiException, Long originalInput);

}
