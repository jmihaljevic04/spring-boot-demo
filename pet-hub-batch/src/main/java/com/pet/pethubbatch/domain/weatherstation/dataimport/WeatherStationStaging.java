package com.pet.pethubbatch.domain.weatherstation.dataimport;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;

@Getter
@ToString
@EqualsAndHashCode
@Entity
@Immutable
@Table(name = "weather_station_staging")
public class WeatherStationStaging {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "name")
    private String name;

    @Column(name = "timezone")
    private String timezone;

    @Column(name = "latitude")
    private BigDecimal latitude;

    @Column(name = "longitude")
    private BigDecimal longitude;

    @Column(name = "elevation")
    private BigDecimal elevation;

    @Column(name = "country")
    private String country;

    @Column(name = "import_id")
    private Long importId;

    @Column(name = "enabled")
    private Boolean enabled;

}
