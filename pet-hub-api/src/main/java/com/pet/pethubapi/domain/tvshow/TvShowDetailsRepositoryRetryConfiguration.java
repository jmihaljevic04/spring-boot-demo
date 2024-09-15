package com.pet.pethubapi.domain.tvshow;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class TvShowDetailsRepositoryRetryConfiguration {

    /**
     * @return value expressed in milliseconds
     */
    @Bean
    int backoffDelay() {
        return 2500;
    }

    @Bean
    double backoffDelayMultiplier() {
        return 1.25;
    }

    /**
     * @return value expressed in milliseconds
     */
    @Bean
    int backoffDelayMaxValue() {
        return 4500;
    }

}
