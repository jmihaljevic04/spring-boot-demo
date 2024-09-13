package com.pet.pethubapi.application.tvshow;

import com.pet.pethubapi.domain.tvshow.TvShowDTO;
import com.pet.pethubapi.domain.tvshow.TvShowSearchResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TvShowDetailsService {

    List<TvShowDTO> getAllShows(Pageable pageable);

    /**
     * Fetch show details via fuzzy search, returning multiple results sorted by matched score descending.
     */
    List<TvShowSearchResponse> getShowDetailsByName(String showName);

}
