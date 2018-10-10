package com.blackbaud.service.core;

import com.blackbaud.service.api.RestClientRandomBuilderSupport;
import com.blackbaud.testsupport.RandomGenerator;
import lombok.experimental.Delegate;

public class CoreARandom {

    public static final CoreARandom aRandom = new CoreARandom();

    @Delegate
    public CoreRandomBuilderSupport coreRandomBuilderSupport = new CoreRandomBuilderSupport();
    @Delegate
    private RestClientRandomBuilderSupport restClientRandomBuilderSupport = new RestClientRandomBuilderSupport();
    @Delegate
    private RandomGenerator randomGenerator = new RandomGenerator();

}
