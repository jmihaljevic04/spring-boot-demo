package com.pet.pethubbatch.domain.weatherstation.dataimport;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WeatherStationImportStatusEnum {

    REQUESTED(1, "manually requested"),
    STARTED(2, "started by scheduler"),
    IN_PROGRESS(3, "in progress"),
    COMPLETED(4, "successfully completed"),
    FAILED(5, "failed");

    private final Integer id;
    private final String description;

}
