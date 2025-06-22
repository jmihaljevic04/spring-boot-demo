package com.pet.pethubbatch.domain.weatherstation.dataimport;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeatherStationStagingRepository extends JpaRepository<WeatherStationStaging, Long> {

    List<WeatherStationStaging> findAllByImportId(Long importId);

}
