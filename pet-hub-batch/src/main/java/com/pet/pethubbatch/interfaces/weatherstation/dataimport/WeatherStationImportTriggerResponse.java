package com.pet.pethubbatch.interfaces.weatherstation.dataimport;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

@Getter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class WeatherStationImportTriggerResponse extends RepresentationModel<WeatherStationImportTriggerResponse> {

    private final Long importId;
    private final String status;

}
