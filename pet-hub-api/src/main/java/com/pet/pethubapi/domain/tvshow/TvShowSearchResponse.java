package com.pet.pethubapi.domain.tvshow;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public final class TvShowSearchResponse {

    private double score;
    private TvShowDTO show;

}
