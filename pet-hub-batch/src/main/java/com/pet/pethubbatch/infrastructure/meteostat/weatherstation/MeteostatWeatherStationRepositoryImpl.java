package com.pet.pethubbatch.infrastructure.meteostat.weatherstation;

import com.pet.pethubbatch.application.ObjectMapperUtils;
import com.pet.pethubbatch.domain.weatherstation.meteostat.MeteostatWeatherStationDTO;
import com.pet.pethubbatch.domain.weatherstation.meteostat.MeteostatWeatherStationRepository;
import com.pet.pethubbatch.infrastructure.meteostat.MeteostatIntegrationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

@Slf4j
@Repository
@RequiredArgsConstructor
class MeteostatWeatherStationRepositoryImpl implements MeteostatWeatherStationRepository {

    private final RestClient meteostatRestClient;

    @NonNull
    @Override
    public Path downloadImportFile() throws MeteostatIntegrationException {
        try {
            final var downloadedFile = Files
                .createTempFile(IMPORT_FILE_PREFIX + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME), ".json");

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

                deleteOldFiles(downloadedFile.getFileName());
                return downloadedFile;
            }
        } catch (IOException e) {
            throw new MeteostatIntegrationException("Unable to read or transform weather station import file!", e);
        } catch (Exception e) {
            throw new MeteostatIntegrationException("Unable to download weather station import!", e);
        }
    }

    private void deleteOldFiles(Path downloadedFileName) {
        final var tempPath = Paths.get(System.getProperty("java.io.tmpdir"));
        try (Stream<Path> files = Files.list(tempPath)) {
            files
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().startsWith(IMPORT_FILE_PREFIX) && !path.getFileName().equals(downloadedFileName))
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        log.error("Failed to delete weather station old import file: {}!", path.toAbsolutePath(), e);
                    }
                });
        } catch (IOException e) {
            log.error("Failed to access temporary directory!", e);
        }
    }

}
