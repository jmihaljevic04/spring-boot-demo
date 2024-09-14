package com.pet.pethubapi.domain;

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
    @Setter
    public static final class TvMazeProperties {

        private TvMazeRateLimitProperties rateLimit;
        private TvMazeSearchProperties search;
        private TvMazeShowIndexProperties showIndex;

        @Getter
        @Setter
        public static final class TvMazeRateLimitProperties {

            private int maxDaily;
            private int maxPerInterval;
            private int timeInterval;

        }

        @Getter
        @Setter
        public static final class TvMazeSearchProperties {

            private String baseUrl;

        }

        @Getter
        @Setter
        public static final class TvMazeShowIndexProperties {

            private String baseUrl;

        }

    }

}
