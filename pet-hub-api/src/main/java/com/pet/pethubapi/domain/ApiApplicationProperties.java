package com.pet.pethubapi.domain;

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
@EnableConfigurationProperties(ApiApplicationProperties.class)
public class ApiApplicationProperties {

    private final TvMazeProperties tvMaze = new TvMazeProperties();

    @Getter
    @Setter(AccessLevel.PACKAGE)
    public static final class TvMazeProperties {

        private TvMazeRateLimitProperties rateLimit;
        private TvMazeSearchProperties search;
        private TvMazeShowIndexProperties showIndex;

        @Getter
        @Setter(AccessLevel.PACKAGE)
        public static final class TvMazeRateLimitProperties {

            /**
             * maximum number of API calls daily
             */
            private int maxDaily;
            /**
             * maximum number of API calls per predefined time interval
             */
            private int maxPerInterval;
            /**
             * time interval for maximum number of API calls; defined in seconds
             */
            private int timeInterval;

        }

        @Getter
        @Setter(AccessLevel.PACKAGE)
        public static final class TvMazeSearchProperties {

            private String baseUrl;

        }

        @Getter
        @Setter(AccessLevel.PACKAGE)
        public static final class TvMazeShowIndexProperties {

            private String baseUrl;

        }

    }

}
