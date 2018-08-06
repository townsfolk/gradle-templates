package com.blackbaud.service.config;

import com.blackbaud.cosmos.config.MongoCosmosConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = ("com.blackbaud.service.core.domain"))
public class CosmosConfig extends MongoCosmosConfig {

    @Value("${spring.data.mongodb.uri}")
    private String databaseUri;

    @Value("${spring.application.name}-db")
    private String databaseName;

    @Override
    public String getDatabaseUri() {
        return databaseUri;
    }

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Bean
    public TruckRepository truckRepository(CosmosRetryableRepositoryFactory factory, TruckTransactionalRepository transactionalRepository) {
        return factory.createRepository(transactionalRepository, TruckRepository.class);
    }

}
