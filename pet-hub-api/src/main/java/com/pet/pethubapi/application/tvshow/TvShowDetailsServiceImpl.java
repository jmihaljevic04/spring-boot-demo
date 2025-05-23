package com.pet.pethubapi.application.tvshow;

import com.pet.pethubapi.domain.tvshow.TvShowDTO;
import com.pet.pethubapi.domain.tvshow.TvShowDetailsRepository;
import com.pet.pethubapi.domain.tvshow.TvShowSearchResponse;
import io.micrometer.core.annotation.Timed;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Observed(name = "tv-show-service")
@RequiredArgsConstructor
@Service
class TvShowDetailsServiceImpl implements TvShowDetailsService {

    public static final double SHOW_MATCHED_SCORE_MIN_THRESHOLD = 0.5D;

    private final TvShowDetailsRepository showDetailsRepository;

    @Timed(value = "tv-show.get-all", description = "Duration for fetching all TV shows page-by-page")
    @Override
    public List<TvShowDTO> getAllShows(Pageable pageable) {
        return showDetailsRepository.getAllShows(pageable.getPageNumber());
    }

    @Override
    public List<TvShowSearchResponse> getShowDetailsByName(final String showName) {
        if (StringUtils.isBlank(showName)) {
            throw new InvalidShowDetailsActionException("TV Show name is empty!");
        }

        final var normalizedName = StringUtils.normalizeSpace(showName.replaceAll("\\P{Print}", ""));

        final List<TvShowSearchResponse> result = showDetailsRepository.getShowDetailsByName(normalizedName);
        final var filteredResults = result.stream()
            .filter(response -> response.getScore() >= SHOW_MATCHED_SCORE_MIN_THRESHOLD)
            .toList();

        if (filteredResults.isEmpty()) {
            throw new InvalidShowDetailsActionException("TV Show with name: '" + normalizedName + "' not found!");
        }

        return filteredResults;
    }

    @Override
    public TvShowDTO getShowDetailsById(Long showId) {
        if (showId == null || showId <= 0) {
            throw new InvalidShowDetailsActionException("TV Show ID is invalid!");
        }

        return showDetailsRepository.getShowDetailsById(showId)
            .orElseThrow(() -> new InvalidShowDetailsActionException("TV Show with ID: " + showId + " not found!"));
    }

}
