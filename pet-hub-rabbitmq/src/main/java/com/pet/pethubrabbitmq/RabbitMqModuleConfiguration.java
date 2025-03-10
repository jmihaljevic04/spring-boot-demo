package com.pet.pethubrabbitmq;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ComponentScan
@AutoConfiguration
@AutoConfigurationPackage
class RabbitMqModuleConfiguration {

}
