package com.pet.pethubbatch.application.weatherstation;

import com.pet.pethubbatch.domain.weatherstation.WeatherStationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class ImportWeatherStationServiceImpl implements ImportWeatherStationService {

    private final WeatherStationRepository weatherStationRepository;

    @Override
    public Long triggerImport() {
        return -1L;
    }

}
