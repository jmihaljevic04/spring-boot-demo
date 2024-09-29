package com.pet.pethubapi.domain.tvshow;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public final class TvShowRating implements Serializable {

    private Double average;

}
