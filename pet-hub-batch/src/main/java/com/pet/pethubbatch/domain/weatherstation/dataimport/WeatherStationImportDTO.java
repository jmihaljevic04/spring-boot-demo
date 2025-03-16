package com.pet.pethubbatch.domain.weatherstation.dataimport;

import java.time.LocalDateTime;

public record WeatherStationImportDTO(Long id,
                                      String requestedBy,
                                      LocalDateTime startedAt,
                                      LocalDateTime finishedAt,
                                      WeatherStationImportStatusEnum status) {

    public static WeatherStationImportDTO from(WeatherStationImport entity) {
        return new WeatherStationImportDTO(entity.getId(),
            entity.getRequestedBy(),
            entity.getStartedAt(),
            entity.getFinishedAt(),
            entity.getStatus().getName());
    }

}
