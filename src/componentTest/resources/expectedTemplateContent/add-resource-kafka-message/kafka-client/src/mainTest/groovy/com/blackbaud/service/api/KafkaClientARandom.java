package com.blackbaud.service.api;

import com.blackbaud.testsupport.RandomGenerator;
import lombok.experimental.Delegate;

public class KafkaClientARandom {

    public static final KafkaClientARandom aRandom = new KafkaClientARandom();

    @Delegate
    private KafkaClientRandomBuilderSupport randomClientBuilderSupport = new KafkaClientRandomBuilderSupport();
    @Delegate
    private RandomGenerator randomGenerator = new RandomGenerator();

}
