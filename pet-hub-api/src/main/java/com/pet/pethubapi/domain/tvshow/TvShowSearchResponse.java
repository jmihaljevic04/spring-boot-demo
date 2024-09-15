package com.pet.pethubapi.domain.tvshow;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
public final class TvShowSearchResponse extends RepresentationModel<TvShowSearchResponse> {

    private double score;
    private TvShowDTO show;

}
