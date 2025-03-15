package com.pet.pethubbatch.application.weatherstation;

public interface ImportWeatherStationService {

    Long NOOP_IMPORT_ID = -1L;

    /**
     * Trigger asynchronous import of weather stations. No-op if scheduled import is enabled.
     *
     * @return ID of started import
     */
    Long triggerImport();

}
