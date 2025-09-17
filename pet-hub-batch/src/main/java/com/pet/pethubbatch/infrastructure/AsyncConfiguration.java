package com.pet.pethubbatch.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@EnableAsync
@Configuration
class AsyncConfiguration {

    @Bean(name = "taskExecutor")
    @ConditionalOnProperty(value = "pet.use-virtual-threads", havingValue = "false", matchIfMissing = true)
    public ThreadPoolTaskExecutor taskExecutor() {
        final var corePoolSize = Runtime.getRuntime().availableProcessors() + 1;
        final var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(corePoolSize * 3);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("pet-hub-batch-");
        executor.setRejectedExecutionHandler((task, ex) -> log.warn("Task rejected: active={}, pool={}, queue={}",
            ex.getActiveCount(), ex.getPoolSize(), ex.getQueue().size()));
        executor.initialize();
        return executor;
    }

    @Bean(name = "taskExecutor")
    @ConditionalOnProperty(value = "pet.use-virtual-threads", havingValue = "true")
    public SimpleAsyncTaskExecutor virtualTaskExecutor() {
        final var executor = new SimpleAsyncTaskExecutor();
        executor.setVirtualThreads(true);
        executor.setThreadNamePrefix("vt-pet-hub-batch-");
        executor.setConcurrencyLimit(200);
        return executor;
    }

}
