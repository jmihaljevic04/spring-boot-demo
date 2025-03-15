package com.pet.pethubbatch.domain.weatherstation.meteostat;

import java.nio.file.Path;

public interface MeteostatWeatherStationRepository {

    Path downloadImportFile();

}
