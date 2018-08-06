package com.blackbaud.service.servicebus;

import com.blackbaud.azure.servicebus.consumer.ServiceBusMessage;
import com.blackbaud.azure.servicebus.consumer.handlers.MessageHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatasyncMessageHandler implements MessageHandler<DatasyncPayload> {

    @Override
    public void process(ServiceBusMessage<DatasyncPayload> message) {
        DatasyncPayload payload = message.getPayload();
    }

}
