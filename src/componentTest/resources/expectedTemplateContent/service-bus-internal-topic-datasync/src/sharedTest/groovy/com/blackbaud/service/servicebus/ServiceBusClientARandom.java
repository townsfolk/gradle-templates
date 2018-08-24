package com.blackbaud.service.servicebus;

import com.blackbaud.testsupport.RandomGenerator;
import lombok.experimental.Delegate;

public class ServiceBusClientARandom {

    public static final ServiceBusClientARandom aRandom = new ServiceBusClientARandom();

    @Delegate
    private ServiceBusClientRandomBuilderSupport randomClientBuilderSupport = new ServiceBusClientRandomBuilderSupport();
    @Delegate
    private RandomGenerator randomGenerator = new RandomGenerator();

}
