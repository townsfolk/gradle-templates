package com.blackbaud.service;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import com.blackbaud.security.CoreSecurityEcosystemParticipantRequirementsProvider;
import com.blackbaud.boot.config.CommonSpringConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.blackbaud.service.core", "com.blackbaud.service.resources"})
@EntityScan({"com.blackbaud.service", "com.blackbaud.boot.converters"})
public class Service extends CommonSpringConfig {

    @Bean
    public CoreSecurityEcosystemParticipantRequirementsProvider coreSecurityEcosystemParticipantRequirementsProvider() {
        return new CoreSecurityEcosystemParticipantRequirementsProvider();
    }

    public static void main(String[] args) {
        SpringApplication.run(Service.class, args);
    }

}
