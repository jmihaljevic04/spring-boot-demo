package com.pet.pethubbatch.domain.country;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, Long> {

    @Query("SELECT c.isoCode FROM Country c WHERE c.isoCode IN ('HR')")
    List<String> findCountriesEligibleForWeatherStationImport();

    Country findByIsoCode(String isoCode);

}
