package com.pet.pethubapi.domain.tvshow;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public final class TvShowDTO implements Serializable {

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
    private TvShowRating rating;
    private Map<String, String> externals; // object containing external IDs for different show websites (e.g. IMDb)
    @JsonDeserialize(converter = EpochTimestampToLocalDateTimeConverter.class)
    private LocalDateTime updated; // epoch format

    private static final class EpochTimestampToLocalDateTimeConverter extends StdConverter<Long, LocalDateTime> {

        @Override
        public LocalDateTime convert(final Long value) {
            return Instant.ofEpochSecond(value).atZone(ZoneId.systemDefault()).toLocalDateTime();
        }

    }

}
