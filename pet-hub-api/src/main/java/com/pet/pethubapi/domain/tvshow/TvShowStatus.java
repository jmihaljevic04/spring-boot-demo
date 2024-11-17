package com.pet.pethubapi.domain.tvshow;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TvShowStatus {

    RUNNING("Running"), ENDED("Ended"), TBD("To Be Determined"), IN_DEVELOPMENT("In Development");

    @JsonValue
    private final String value;

}
