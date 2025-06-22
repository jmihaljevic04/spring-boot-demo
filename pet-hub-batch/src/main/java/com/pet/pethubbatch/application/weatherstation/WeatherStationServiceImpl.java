package com.pet.pethubbatch.application.weatherstation;

import com.pet.pethubbatch.domain.country.CountryRepository;
import com.pet.pethubbatch.domain.weatherstation.WeatherStation;
import com.pet.pethubbatch.domain.weatherstation.WeatherStationRepository;
import com.pet.pethubbatch.domain.weatherstation.dataimport.WeatherStationImportRepository;
import com.pet.pethubbatch.domain.weatherstation.dataimport.WeatherStationStaging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherStationServiceImpl implements WeatherStationService {

    private final WeatherStationRepository weatherStationRepository;
    private final CountryRepository countryRepository;
    private final WeatherStationImportRepository weatherStationImportRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void upsertFromStaging(List<WeatherStationStaging> weatherStationUpdates) {
        for (var weatherStationUpdate : weatherStationUpdates) {
            final var weatherStation = weatherStationRepository.findWeatherStationByExternalId(weatherStationUpdate.getExternalId());
            final WeatherStation actualWeatherStation;
            if (weatherStation.isPresent()) {
                actualWeatherStation = weatherStation.get();
                if (!actualWeatherStation.getCountry().getIsoCode().equals(weatherStationUpdate.getCountry())) {
                    // TODO: implement history/auditing of updates, and update importId
                    log.warn("Weather station with ID: {} has changed country to {}!",
                        actualWeatherStation.getId(), weatherStationUpdate.getCountry());
                }
            } else {
                actualWeatherStation = new WeatherStation();
                actualWeatherStation.setExternalId(weatherStationUpdate.getExternalId());
                actualWeatherStation.setElevation(weatherStationUpdate.getElevation());
                actualWeatherStation.setLatitude(weatherStationUpdate.getLatitude());
                actualWeatherStation.setLongitude(weatherStationUpdate.getLongitude());
                actualWeatherStation.setTimezone(weatherStationUpdate.getTimezone());
                actualWeatherStation.setCountry(countryRepository.findByIsoCode(weatherStationUpdate.getCountry()));
                actualWeatherStation.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            }

            actualWeatherStation.setName(weatherStationUpdate.getName());
            actualWeatherStation.setLastUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            actualWeatherStation.setWeatherStationImport(weatherStationImportRepository.getReferenceById(weatherStationUpdate.getImportId()));

            weatherStationRepository.save(actualWeatherStation);
        }
    }

}
