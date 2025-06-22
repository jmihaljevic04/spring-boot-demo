package com.pet.pethubbatch.application.weatherstation.dataimport;

import com.pet.pethubbatch.application.FileUtils;
import com.pet.pethubbatch.application.weatherstation.WeatherStationService;
import com.pet.pethubbatch.domain.BatchApplicationProperties;
import com.pet.pethubbatch.domain.country.CountryRepository;
import com.pet.pethubbatch.domain.weatherstation.dataimport.WeatherStationImport;
import com.pet.pethubbatch.domain.weatherstation.dataimport.WeatherStationImportDTO;
import com.pet.pethubbatch.domain.weatherstation.dataimport.WeatherStationImportRepository;
import com.pet.pethubbatch.domain.weatherstation.dataimport.WeatherStationImportStatus;
import com.pet.pethubbatch.domain.weatherstation.dataimport.WeatherStationImportStatusEnum;
import com.pet.pethubbatch.domain.weatherstation.dataimport.WeatherStationStaging;
import com.pet.pethubbatch.domain.weatherstation.dataimport.WeatherStationStagingRepository;
import com.pet.pethubbatch.domain.weatherstation.meteostat.MeteostatWeatherStationRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

import static com.pet.pethubbatch.domain.weatherstation.dataimport.WeatherStationImport.DESCRIPTION_LENGTH;

@Slf4j
@Service
@RequiredArgsConstructor
class ImportWeatherStationServiceImpl implements ImportWeatherStationService {

    private final EntityManager em;
    private final DataSource dataSource;
    private final CountryRepository countryRepository;
    private final WeatherStationService weatherStationService;
    private final BatchApplicationProperties applicationProperties;
    private final WeatherStationImportRepository weatherStationImportRepository;
    private final WeatherStationStagingRepository weatherStationStagingRepository;
    private final MeteostatWeatherStationRepository meteostatWeatherStationRepository;

    @Override
    public WeatherStationImportDTO getImportStatus(Long importId) {
        return weatherStationImportRepository.findById(importId)
            .map(WeatherStationImportDTO::from)
            .orElseThrow(() -> new InvalidWeatherStationImportActionException(
                "Weather station import doesn't exist with ID: " + importId + "!", HttpStatus.NOT_FOUND));
    }

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
    @Transactional
    public void startImport(Long importId) {
        log.info("Started weather station import with ID: {}", importId);
        final WeatherStationImport anImport = weatherStationImportRepository.getReferenceById(importId);

        final Path downloadedImportFile;
        try {
            downloadedImportFile = meteostatWeatherStationRepository.downloadImportFile();
            log.info("Downloaded weather station import file: {} for import ID: {}", anImport.getFilePath(), importId);

            anImport.setFilePath(downloadedImportFile.toAbsolutePath().toString());
            anImport.setFileHash(FileUtils.calculateHashCode(downloadedImportFile));
        } catch (Exception e) {
            log.warn("Failed to download and extract weather station import file for import ID: {}!", importId, e);
            markImportAsFailed(anImport, e);
            return;
        }

        final var lastImportedHash = weatherStationImportRepository.getLastImportedHashCode();
        if (lastImportedHash.equals(anImport.getFileHash())) {
            markImportAsSkipped(anImport, lastImportedHash, downloadedImportFile);
            return;
        }

        anImport.setStatus(new WeatherStationImportStatus(WeatherStationImportStatusEnum.IN_PROGRESS));
        weatherStationImportRepository.save(anImport);
    }

    @Override
    @Transactional
    public void insertImportFile(Long importId) {
        log.info("Inserting weather station import file with import ID: {}...", importId);
        if (!isImportInProgress(importId)) {
            return;
        }

        // create temp table
        em.createNativeQuery("CREATE TEMP TABLE weather_station_json_temp (file_content JSONB)");

        // insert data into temp table
        final var anImport = weatherStationImportRepository.getReferenceById(importId);
        try (Connection conn = dataSource.getConnection(); InputStream in = Files.newInputStream(Path.of(anImport.getFilePath()))) {
            PGConnection pgConn = conn.unwrap(PGConnection.class);
            CopyManager copyManager = pgConn.getCopyAPI();

            copyManager.copyIn("COPY weather_station_json_temp FROM STDIN WITH (encoding 'UTF-8')", in);
        } catch (Exception e) {
            log.warn("Failed to insert weather station import data to database import ID: {}!", importId, e);
            markImportAsFailed(anImport, e);
            return;
        }

        // import to staging table
        try {
            em.createNativeQuery("TRUNCATE weather_station_staging").executeUpdate();
            em.createNativeQuery("CALL weather_station_import(:import_id, :countries)")
                .setParameter("import_id", importId)
                .setParameter("countries", countryRepository.findCountriesEligibleForWeatherStationImport())
                .executeUpdate();
        } catch (Exception e) {
            log.warn("Failed to insert weather station import data to staging for import ID: {}!", importId, e);
            markImportAsFailed(anImport, e);
        }
    }

    @Override
    @Transactional
    public void importWeatherStations(Long importId) {
        log.info("Importing weather station updates with import ID: {}...", importId);
        if (!isImportInProgress(importId)) {
            return;
        }

        final List<WeatherStationStaging> weatherStationUpdates = weatherStationStagingRepository.findAllByImportId(importId);
        ListUtils.partition(weatherStationUpdates, applicationProperties.getMeteostat().getWeatherStation().getImportBatchSize())
            .forEach(weatherStationUpdateBatch -> {
                weatherStationService.upsertFromStaging(weatherStationUpdateBatch);
                em.detach(weatherStationUpdateBatch);
            });
    }

    private boolean isImportInProgress(Long importId) {
        final WeatherStationImport anImport = weatherStationImportRepository.getReferenceById(importId);
        if (!WeatherStationImportStatusEnum.IN_PROGRESS.equals(anImport.getStatus().getName())) {
            log.info("Weather station import with ID: {} isn't in progress anymore!", importId);
            return false;
        }
        return true;
    }

    private void markImportAsFailed(WeatherStationImport anImport, Exception exception) {
        anImport.setFinishedAt(LocalDateTime.now());
        anImport.setStatus(new WeatherStationImportStatus(WeatherStationImportStatusEnum.FAILED));
        anImport.setStatusDescription(StringUtils.abbreviate(ExceptionUtils.getRootCauseMessage(exception), DESCRIPTION_LENGTH));
        weatherStationImportRepository.save(anImport);
    }

    private void markImportAsSkipped(WeatherStationImport anImport, String lastImportedHash, Path downloadedImportFile) {
        anImport.setStatus(new WeatherStationImportStatus(WeatherStationImportStatusEnum.SKIPPED));
        anImport.setStatusDescription("No changes in file hash comparing last import for hash: " + lastImportedHash);
        weatherStationImportRepository.save(anImport);

        try {
            Files.delete(downloadedImportFile);
        } catch (IOException e) {
            log.error("Failed to delete downloaded weather station duplicate import file: {}!", downloadedImportFile.toAbsolutePath(), e);
            anImport.setStatusDescription(anImport.getStatusDescription()
                .concat(". Failed to delete downloaded weather station duplicate import file."));
        }
    }

}
