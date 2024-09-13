package com.pet.pethubapi.domain.tvshow;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public final class TvShowDTO {

    private Long id;
    private String url;
    private String name;
    private String language;
    private List<String> genres;
    private TvShowStatus status;
    private Integer runtime;
    private Integer averageRuntime;
    private LocalDate premiered;
    private LocalDate ended;
    private String officialSite;
    private String country;
    private TvShowRating rating;
    private Map<String, String> externals; // object containing external IDs for different show websites (e.g. IMDb)
    private Long updated; // epoch format

}
