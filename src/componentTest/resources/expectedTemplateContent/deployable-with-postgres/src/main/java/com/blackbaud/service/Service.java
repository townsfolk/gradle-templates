package com.blackbaud.service;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import com.blackbaud.boot.config.WebMvcRestServiceConfig;
import com.blackbaud.service.core.CoreConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan("com.blackbaud.service.resources")
@Import({
        CoreConfig.class,
        WebMvcRestServiceConfig.class,
})
@EntityScan({"com.blackbaud.service", "com.blackbaud.boot.converters"})
public class Service {

    public static void main(String[] args) {
        SpringApplication.run(Service.class, args);
    }

}
