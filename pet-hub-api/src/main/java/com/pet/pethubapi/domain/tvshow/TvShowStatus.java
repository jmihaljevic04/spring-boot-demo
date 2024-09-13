package com.pet.pethubapi.domain.tvshow;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TvShowStatus {

    RUNNING("Running"), ENDED("Ended");

    private final String value;

}
