package com.pet.pethubbatch.domain.weatherstation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeatherStationRepository extends JpaRepository<WeatherStation, Long> {

    Optional<WeatherStation> findWeatherStationByExternalId(String externalId);

}
