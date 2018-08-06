package com.blackbaud.service.servicebus;

import com.blackbaud.azure.servicebus.config.ServiceBusProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "servicebus.datasync")
public class DatasyncServiceBusProperties extends ServiceBusProperties {
}