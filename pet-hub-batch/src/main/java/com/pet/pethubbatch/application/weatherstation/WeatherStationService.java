package com.pet.pethubbatch.application.weatherstation;

import com.pet.pethubbatch.domain.weatherstation.dataimport.WeatherStationStaging;

import java.util.List;

public interface WeatherStationService {

    void upsertFromStaging(List<WeatherStationStaging> weatherStationUpdates);

}
