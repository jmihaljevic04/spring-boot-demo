package com.pet.pethubbatch.domain.weatherstation.dataimport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WeatherStationImportRepository extends JpaRepository<WeatherStationImport, Long> {

    @Query("select case when count(import) > 0 then true else false end from WeatherStationImport import where import.finishedAt is null")
    boolean isAnyImportActive();

}
