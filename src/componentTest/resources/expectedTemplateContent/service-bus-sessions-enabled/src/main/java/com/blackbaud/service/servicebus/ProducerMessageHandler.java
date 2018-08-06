package com.blackbaud.service.servicebus;

import com.blackbaud.azure.servicebus.consumer.ServiceBusMessage;
import com.blackbaud.azure.servicebus.consumer.handlers.MessageHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProducerMessageHandler implements MessageHandler<ProducerPayload> {

    @Override
    public void process(ServiceBusMessage<ProducerPayload> message) {
        ProducerPayload payload = message.getPayload();
    }

}
