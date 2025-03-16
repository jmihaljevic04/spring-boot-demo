package com.pet.pethubbatch.application.weatherstation.dataimport;

import com.pet.pethubbatch.domain.BatchApplicationProperties;
import com.pet.pethubbatch.domain.weatherstation.dataimport.WeatherStationImport;
import com.pet.pethubbatch.domain.weatherstation.dataimport.WeatherStationImportDTO;
import com.pet.pethubbatch.domain.weatherstation.dataimport.WeatherStationImportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class ImportWeatherStationServiceImpl implements ImportWeatherStationService {

    private final BatchApplicationProperties applicationProperties;
    private final WeatherStationImportRepository weatherStationImportRepository;

    @Override
    public Long triggerImport() {
        final var isScheduledImportEnabled = BooleanUtils
            .isTrue(applicationProperties.getMeteostat().getWeatherStation().getScheduledImportEnabled());
        final var isAnyImportActive = weatherStationImportRepository.isAnyImportActive();
        if (isScheduledImportEnabled || isAnyImportActive) {
            return NOOP_IMPORT_ID;
        }

        var anImport = WeatherStationImport.manual(SecurityContextHolder.getContext().getAuthentication().getName());
        anImport = weatherStationImportRepository.save(anImport);
        return anImport.getId();
    }

    @Override
    public WeatherStationImportDTO getImportStatus(Long importId) {
        return weatherStationImportRepository.findById(importId)
            .map(WeatherStationImportDTO::from)
            .orElseThrow(() -> new InvalidWeatherStationImportActionException(
                "Weather station import doesn't exist with ID: " + importId + "!", HttpStatus.NOT_FOUND));
    }

}
