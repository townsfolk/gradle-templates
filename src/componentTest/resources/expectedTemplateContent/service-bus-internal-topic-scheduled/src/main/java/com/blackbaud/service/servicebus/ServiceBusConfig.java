package com.blackbaud.service.servicebus;

import com.blackbaud.azure.servicebus.config.ServiceBusProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import com.blackbaud.azure.servicebus.config.ServiceBusConsumerConfig;
import com.blackbaud.azure.servicebus.config.ServiceBusPublisherConfig;
import com.blackbaud.azure.servicebus.consumer.ServiceBusConsumer;
import com.blackbaud.azure.servicebus.consumer.ServiceBusConsumerBuilder;
import com.blackbaud.azure.servicebus.publisher.JsonMessagePublisher;
import com.blackbaud.azure.servicebus.publisher.ServiceBusPublisherBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ServiceBusConsumerConfig.class, ServiceBusPublisherConfig.class})
@EnableConfigurationProperties(ScheduledServiceBusProperties.class)
public class ServiceBusConfig {

    @Bean
    public JsonMessagePublisher scheduledPublisher(
            ServiceBusPublisherBuilder.Factory serviceBusPublisherFactory,
            ScheduledServiceBusProperties serviceBusProperties) {
        return serviceBusPublisherFactory.create()
                .buildJsonPublisher(serviceBusProperties);
    }

    @Bean
    public ScheduledMessageHandler scheduledMessageHandler() {
        return new ScheduledMessageHandler();
    }

    @Bean
    public ServiceBusConsumer scheduledConsumer(
            ServiceBusConsumerBuilder.Factory serviceBusConsumerFactory,
            ScheduledMessageHandler scheduledMessageHandler,
            ScheduledServiceBusProperties serviceBusProperties) {
        return serviceBusConsumerFactory.create()
                .schedulingTopicServiceBus(serviceBusProperties)
                .jsonMessageHandler(scheduledMessageHandler, ScheduledPayload.class)
                .build();
    }

}
