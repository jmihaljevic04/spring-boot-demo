package com.pet.pethubapi.domain.tvshow;

import java.util.List;

public interface TvShowDetailsRepository {

    List<TvShowDTO> getAllShows(int pageNumber);

    List<TvShowSearchResponse> getShowDetailsByName(String name);

}
