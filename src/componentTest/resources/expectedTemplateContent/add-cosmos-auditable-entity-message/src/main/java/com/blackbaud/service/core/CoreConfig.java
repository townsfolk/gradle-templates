package com.blackbaud.service.core;

import com.blackbaud.service.core.CosmosConfig;
import com.blackbaud.feign.JacksonFeignBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan("com.blackbaud.service.core")
@Import(CosmosConfig.class)
public class CoreConfig {

    @Bean
    public JacksonFeignBuilder jacksonFeignBuilder() {
        return new JacksonFeignBuilder();
    }

}
