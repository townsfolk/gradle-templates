package com.blackbaud.service.api;

import com.blackbaud.testsupport.RandomGenerator;
import lombok.experimental.Delegate;

public class EventhubsClientARandom {

    public static final EventhubsClientARandom aRandom = new EventhubsClientARandom();

    @Delegate
    private EventhubsClientRandomBuilderSupport randomClientBuilderSupport = new EventhubsClientRandomBuilderSupport();
    @Delegate
    private RandomGenerator randomGenerator = new RandomGenerator();

}
