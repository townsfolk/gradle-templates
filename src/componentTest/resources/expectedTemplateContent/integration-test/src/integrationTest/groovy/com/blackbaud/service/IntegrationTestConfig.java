package com.blackbaud.service;

import com.blackbaud.service.core.CoreRandomBuilderSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.blackbaud.service.core.CoreARandom.aRandom;

@Configuration
class IntegrationTestConfig {

    @Bean
    CoreRandomBuilderSupport coreRandomBuilderSupport() {
        return aRandom.coreRandomBuilderSupport;
    }

}