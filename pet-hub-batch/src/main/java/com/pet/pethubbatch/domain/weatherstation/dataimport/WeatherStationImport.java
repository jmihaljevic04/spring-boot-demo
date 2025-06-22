package com.pet.pethubbatch.domain.weatherstation.dataimport;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "weather_station_import")
public class WeatherStationImport {

    public static final int DESCRIPTION_LENGTH = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "file_path")
    private String filePath;

    @Setter
    @Column(name = "file_hash")
    private String fileHash;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "requested_by")
    private String requestedBy;

    @Column(name = "is_manual_import")
    private Boolean isManualImport;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Setter
    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Setter
    @Column(name = "status_description", length = DESCRIPTION_LENGTH)
    private String statusDescription;

    @Setter
    @ManyToOne
    @JoinColumn(name = "import_status_id", referencedColumnName = "id")
    private WeatherStationImportStatus status;

    public static WeatherStationImport manual(String requestedBy) {
        final var anImport = new WeatherStationImport();
        anImport.requestedAt = LocalDateTime.now();
        anImport.requestedBy = requestedBy;
        anImport.isManualImport = true;
        anImport.status = new WeatherStationImportStatus(WeatherStationImportStatusEnum.REQUESTED);

        return anImport;
    }

    public static WeatherStationImport scheduled() {
        final var anImport = new WeatherStationImport();
        anImport.startedAt = LocalDateTime.now();
        anImport.requestedBy = "system-admin";
        anImport.isManualImport = false;
        anImport.status = new WeatherStationImportStatus(WeatherStationImportStatusEnum.STARTED);

        return anImport;
    }

}
