package com.pet.pethubapi.domain.tvshow;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
public final class TvShowSearchResponse extends RepresentationModel<TvShowSearchResponse> implements Serializable {

    private double score;
    private TvShowDTO show;

}
