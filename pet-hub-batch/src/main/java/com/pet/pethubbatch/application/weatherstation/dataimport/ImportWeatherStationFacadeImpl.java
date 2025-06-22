package com.pet.pethubbatch.application.weatherstation.dataimport;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImportWeatherStationFacadeImpl implements ImportWeatherStationFacade {

    private final ImportWeatherStationService importWeatherStationService;

    @Async
    @Override
    public void importWeatherStations(Long importId) {
        importWeatherStationService.startImport(importId);
        importWeatherStationService.insertImportFile(importId);
        importWeatherStationService.importWeatherStations(importId);
    }

}
