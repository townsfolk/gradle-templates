package com.blackbaud.service.core;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import com.blackbaud.boot.jpa.JpaConfiguration;
import com.blackbaud.feign.JacksonFeignBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan("com.blackbaud.service.core")
@Import(JpaConfiguration.class)
@EntityScan("com.blackbaud.service.core.domain")
@EnableJpaRepositories("com.blackbaud.service.core.domain")
public class CoreConfig {

    @Bean
    public JacksonFeignBuilder jacksonFeignBuilder() {
        return new JacksonFeignBuilder();
    }

}
