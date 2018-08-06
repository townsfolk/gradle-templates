package com.blackbaud.service.core;

import com.blackbaud.service.servicebus.ServiceBusClientRandomBuilderSupport;
import com.blackbaud.testsupport.RandomGenerator;
import lombok.experimental.Delegate;

public class CoreARandom {

    public static final CoreARandom aRandom = new CoreARandom();

    @Delegate
    public CoreRandomBuilderSupport coreRandomBuilderSupport = new CoreRandomBuilderSupport();
    @Delegate
    private ServiceBusClientRandomBuilderSupport serviceBusClientRandomBuilderSupport = new ServiceBusClientRandomBuilderSupport();
    @Delegate
    private RandomGenerator randomGenerator = new RandomGenerator();

}
