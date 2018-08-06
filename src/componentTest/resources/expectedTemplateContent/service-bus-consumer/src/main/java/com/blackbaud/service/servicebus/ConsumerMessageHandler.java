package com.blackbaud.service.servicebus;

import com.blackbaud.azure.servicebus.consumer.ServiceBusMessage;
import com.blackbaud.azure.servicebus.consumer.handlers.MessageHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsumerMessageHandler implements MessageHandler<ConsumerPayload> {

    @Override
    public void process(ServiceBusMessage<ConsumerPayload> message) {
        ConsumerPayload payload = message.getPayload();
    }

}
