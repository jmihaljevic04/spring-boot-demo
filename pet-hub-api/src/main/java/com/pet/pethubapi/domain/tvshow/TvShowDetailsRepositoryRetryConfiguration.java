package com.pet.pethubapi.domain.tvshow;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class TvShowDetailsRepositoryRetryConfiguration {

    /**
     * @return value expressed in milliseconds
     */
    @Bean
    int backoffDelay(@Value("${pet.tvmaze.retry.backoff-delay}") int value) {
        return value;
    }

    @Bean
    double backoffDelayMultiplier(@Value("${pet.tvmaze.retry.backoff-delay-multiplier}") double value) {
        return value;
    }

    /**
     * @return value expressed in milliseconds
     */
    @Bean
    int backoffDelayMaxValue(@Value("${pet.tvmaze.retry.backoff-delay-max-value}") int value) {
        return value;
    }

}
