package com.blackbaud.service.api;

import com.blackbaud.testsupport.RandomGenerator;
import lombok.experimental.Delegate;

public class RestClientARandom {

    public static final RestClientARandom aRandom = new RestClientARandom();

    @Delegate
    private RestClientRandomBuilderSupport randomClientBuilderSupport = new RestClientRandomBuilderSupport();
    @Delegate
    private RandomGenerator randomGenerator = new RandomGenerator();

}
