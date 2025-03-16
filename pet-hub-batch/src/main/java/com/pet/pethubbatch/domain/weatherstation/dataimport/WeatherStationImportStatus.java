package com.pet.pethubbatch.domain.weatherstation.dataimport;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table(name = "weather_station_import_status")
public class WeatherStationImportStatus {

    @Id
    private Integer id;

    @Column
    @Enumerated(value = EnumType.STRING)
    private WeatherStationImportStatusEnum name;

    public WeatherStationImportStatus(WeatherStationImportStatusEnum statusEnum) {
        this.id = statusEnum.getId();
    }

}
