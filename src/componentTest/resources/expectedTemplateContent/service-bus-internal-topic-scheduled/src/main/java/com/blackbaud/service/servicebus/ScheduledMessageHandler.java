package com.blackbaud.service.servicebus;

import com.blackbaud.azure.servicebus.consumer.ServiceBusMessage;
import com.blackbaud.azure.servicebus.consumer.handlers.MessageHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScheduledMessageHandler implements MessageHandler<ScheduledPayload> {

    @Override
    public void process(ServiceBusMessage<ScheduledPayload> message) {
        ScheduledPayload payload = message.getPayload();
    }

}
