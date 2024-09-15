package com.pet.pethubapi.infrastructure.tvmaze;

import com.pet.pethubapi.domain.tvshow.TvShowDTO;
import com.pet.pethubapi.domain.tvshow.TvShowDetailsRepository;
import com.pet.pethubapi.domain.tvshow.TvShowSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        final var responseBody = tvMazeShowIndexRestClient.get()
            .uri(uriBuilder -> uriBuilder.path("/" + id).build())
            .retrieve()
            .body(TvShowDTO.class);

        if (responseBody == null) {
            return Optional.empty();
        }

        return Optional.of(responseBody);
    }

}
