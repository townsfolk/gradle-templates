package com.blackbaud.service;

import com.blackbaud.azure.servicebus.consumer.handlers.ValidatingServiceBusMessageHandler;
import com.blackbaud.azure.servicebus.consumer.ServiceBusConsumerBuilder;
import com.blackbaud.azure.servicebus.consumer.ServiceBusConsumer;
import com.blackbaud.azure.servicebus.config.ServiceBusProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import com.blackbaud.service.core.CoreRandomBuilderSupport;
import com.blackbaud.testsupport.BaseTestConfig;
import com.blackbaud.testsupport.TestClientSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static com.blackbaud.service.core.CoreARandom.aRandom;

@Configuration
@Import({Service.class})
public class ComponentTestConfig extends BaseTestConfig {

    @Autowired
    TestClientSupport testClientSupport;

    @Bean
    CoreRandomBuilderSupport coreRandomBuilderSupport() {
        return aRandom.coreRandomBuilderSupport;
    }

    @Bean
    public ValidatingServiceBusMessageHandler<ProducerPayload> ProducerMessageHandler() {
        return new ValidatingServiceBusMessageHandler<>("ProducerHandler");
    }

    @Bean
    public ServiceBusConsumer sessionConsumer(
            ServiceBusConsumerBuilder.Factory serviceBusConsumerFactory,
            ProducerServiceBusProperties serviceBusProperties,
            @Qualifier("ProducerMessageHandler") ValidatingServiceBusMessageHandler<ProducerPayload> messageHandler) {
        return serviceBusConsumerFactory.create()
                .serviceBus(serviceBusProperties)
                .jsonMessageHandler(messageHandler, ProducerPayload.class, false)
                .build();
    }

}
