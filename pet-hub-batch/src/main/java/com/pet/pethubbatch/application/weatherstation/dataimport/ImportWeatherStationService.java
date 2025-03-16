package com.pet.pethubbatch.application.weatherstation.dataimport;

import com.pet.pethubbatch.domain.weatherstation.dataimport.WeatherStationImportDTO;

public interface ImportWeatherStationService {

    Long NOOP_IMPORT_ID = -1L;

    /**
     * Trigger asynchronous import of weather stations. No-op if scheduled import is enabled or in progress.
     *
     * @return ID of started import
     */
    Long triggerImport();

    WeatherStationImportDTO getImportStatus(Long importId);

}
