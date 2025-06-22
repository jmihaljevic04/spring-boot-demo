package com.pet.pethubbatch.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Getter
@Configuration
@Component("applicationProperties")
@ConfigurationProperties(prefix = "pet")
@EnableConfigurationProperties(BatchApplicationProperties.class)
public class BatchApplicationProperties {

    private final MeteostatProperties meteostat = new MeteostatProperties();

    @Getter
    @Setter(AccessLevel.PACKAGE)
    public static final class MeteostatProperties {

        private String baseUrl;
        private WeatherStationProperties weatherStation = new WeatherStationProperties();

        @Getter
        @Setter(AccessLevel.PACKAGE)
        public static final class WeatherStationProperties {

            private Boolean scheduledImportEnabled;
            private String scheduledImportCron;
            private int importBatchSize;

        }

    }

}
