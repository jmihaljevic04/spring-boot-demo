package com.pet.pethubbatch.application.weatherstation.dataimport;

/**
 * Non-transactional facade wrapping import method invocations in sequential, non-changeable order. Serves for manual import trigger.
 * Method execution is in new thread (asynchronous), making it non-blocking for client.
 */
public interface ImportWeatherStationFacade {

    void importWeatherStations(Long importId);

}
