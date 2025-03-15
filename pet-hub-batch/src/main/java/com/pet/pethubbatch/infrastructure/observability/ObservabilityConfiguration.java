package com.pet.pethubbatch.infrastructure.observability;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ObservabilityConfiguration {

    /**
     * Enables timing method execution using @Timed annotation. Metrics are exported to Prometheus and visible on endpoint "/actuator/prometheus".
     */
    @Bean
    TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    /**
     * Used for RestClient observability via Micrometer. See <a href="https://docs.spring.io/spring-framework/reference/integration/observability.html#observability.http-client.restclient">article</a>.
     */
    @Bean
    ObservationRegistry observationRegistry() {
        return ObservationRegistry.create();
    }

    /**
     * Necessary for using @Observed annotation. Metrics are visible on endpoint: "/actuator/metrics/{metricName}".
     */
    @Bean
    ObservedAspect observedAspect() {
        return new ObservedAspect(observationRegistry());
    }

}
