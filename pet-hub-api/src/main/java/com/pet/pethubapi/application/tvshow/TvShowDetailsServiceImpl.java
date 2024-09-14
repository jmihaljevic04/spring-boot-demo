package com.pet.pethubapi.application.tvshow;

import com.pet.pethubapi.domain.tvshow.TvShowDTO;
import com.pet.pethubapi.domain.tvshow.TvShowDetailsRepository;
import com.pet.pethubapi.domain.tvshow.TvShowSearchResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
class TvShowDetailsServiceImpl implements TvShowDetailsService {

    private static final double SHOW_MATCHED_SCORE_MIN_THRESHOLD = 0.5D;

    private final TvShowDetailsRepository showDetailsRepository;

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
        return result.stream()
            .filter(response -> response.getScore() >= SHOW_MATCHED_SCORE_MIN_THRESHOLD)
            .toList();
    }

}
