package com.pet.pethubapi.infrastructure;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Enables timing method execution using @Timed annotation. Metrics are exported to Prometheus.
 */
@Configuration
class MicrometerTimedAspectConfiguration {

    @Bean
    TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

}
