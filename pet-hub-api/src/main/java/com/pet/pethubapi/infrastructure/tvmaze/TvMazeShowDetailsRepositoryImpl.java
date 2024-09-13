package com.pet.pethubapi.infrastructure.tvmaze;

import com.pet.pethubapi.domain.tvshow.TvShowDTO;
import com.pet.pethubapi.domain.tvshow.TvShowDetailsRepository;
import com.pet.pethubapi.domain.tvshow.TvShowSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
class TvMazeShowDetailsRepositoryImpl implements TvShowDetailsRepository {

    @Override
    public List<TvShowDTO> getAllShows() {
        return List.of(new TvShowDTO());
    }

    @Override
    public List<TvShowSearchResponse> getShowDetailsByName(String name) {
        return List.of(new TvShowSearchResponse());
    }

}
