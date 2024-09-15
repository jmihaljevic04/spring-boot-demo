package com.pet.pethubapi.domain.tvshow;

import java.util.List;
import java.util.Optional;

public interface TvShowDetailsRepository {

    List<TvShowDTO> getAllShows(int pageNumber);

    List<TvShowSearchResponse> getShowDetailsByName(String name);

    Optional<TvShowDTO> getShowDetailsById(Long id);

}
