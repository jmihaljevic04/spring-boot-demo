package com.pet.pethubbatch.domain.weatherstation.meteostat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;
import lombok.Getter;

/**
 * Used for flattening response structure from Meteostat.
 * Property names <b>must not</b> be changed because they are tightly coupled with Meteostat and with SQL procedure importing weather stations.
 *
 * @see com.pet.pethubbatch.domain.weatherstation.dataimport.WeatherStationStaging
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeteostatWeatherStationDTO {

    private String id;
    private String name;
    private String country;
    private String timezone;
    private Double latitude;
    private Double longitude;
    private Double elevation;

    @JsonSetter("name")
    void deserializeName(NameWrapper val) {
        this.name = val != null ? val.en : null;
    }

    @JsonSetter("location")
    void deserializeLatitude(LocationWrapper val) {
        this.latitude = val != null ? val.latitude : null;
        this.longitude = val != null ? val.longitude : null;
        this.elevation = val != null ? val.elevation : null;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @SuppressWarnings("unused")
    private static class NameWrapper {

        private String en;

    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @SuppressWarnings("unused")
    private static class LocationWrapper {

        private Double latitude;
        private Double longitude;
        private Double elevation;

    }

}
