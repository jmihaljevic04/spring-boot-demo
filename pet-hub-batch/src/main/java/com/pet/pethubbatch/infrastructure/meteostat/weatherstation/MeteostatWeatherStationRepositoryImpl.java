package com.pet.pethubbatch.infrastructure.meteostat.weatherstation;

import com.pet.pethubbatch.application.ObjectMapperUtils;
import com.pet.pethubbatch.domain.weatherstation.meteostat.MeteostatWeatherStationDTO;
import com.pet.pethubbatch.domain.weatherstation.meteostat.MeteostatWeatherStationRepository;
import com.pet.pethubbatch.infrastructure.meteostat.MeteostatIntegrationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.zip.GZIPInputStream;

@Repository
@RequiredArgsConstructor
class MeteostatWeatherStationRepositoryImpl implements MeteostatWeatherStationRepository {

    private final RestClient meteostatRestClient;

    @Override
    public Path downloadImportFile() {
        try {
            final var downloadedFile = Files.createTempFile("weather-station-import-" + LocalDateTime.now(), ".json");

            try (var response = meteostatRestClient.get().uri("/stations/lite.json.gz").retrieve().body(InputStream.class);
                 var gzipInputStream = new GZIPInputStream(response);
                 var gzipInputStreamReader = new InputStreamReader(gzipInputStream);
                 var reader = new BufferedReader(gzipInputStreamReader);
                 var writer = Files.newBufferedWriter(downloadedFile)) {

                String line;
                while ((line = reader.readLine()) != null) {
                    var mappedLine = ObjectMapperUtils.readJson(line, MeteostatWeatherStationDTO.class);
                    writer.write(ObjectMapperUtils.writeJson(mappedLine));
                }

                return downloadedFile;
            }
        } catch (IOException e) {
            throw new MeteostatIntegrationException("Unable to download and extract weather station import file!", e);
        }
    }

}
