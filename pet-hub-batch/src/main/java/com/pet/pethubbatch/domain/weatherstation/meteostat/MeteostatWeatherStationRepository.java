package com.pet.pethubbatch.domain.weatherstation.meteostat;

import com.pet.pethubbatch.infrastructure.meteostat.MeteostatIntegrationException;
import org.springframework.lang.NonNull;

import java.nio.file.Path;

public interface MeteostatWeatherStationRepository {

    String IMPORT_FILE_PREFIX = "weather-station-import-";

    /**
     * Downloads "zipped" export file from Meteostat, extracts and transforms it to wanted format. Writes it into temporary file.
     *
     * @return temporary file with extracted JSON format
     * @throws MeteostatIntegrationException in case of error while downloading/reading/transforming downloaded import file
     */
    @NonNull
    Path downloadImportFile() throws MeteostatIntegrationException;

}
