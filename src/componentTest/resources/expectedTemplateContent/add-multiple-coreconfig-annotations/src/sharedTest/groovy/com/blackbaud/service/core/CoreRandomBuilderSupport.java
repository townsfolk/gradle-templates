package com.blackbaud.service.core;

import com.blackbaud.service.core.domain.RandomTruckEntityBuilder;
import com.blackbaud.service.core.domain.TruckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.blackbaud.service.core.domain.RandomAccountEntityBuilder;
import com.blackbaud.service.core.domain.AccountRepository;

public class CoreRandomBuilderSupport {

    @Autowired
    private AccountRepository accountRepository;

    public RandomAccountEntityBuilder accountEntity() {
        return new RandomAccountEntityBuilder(accountRepository);
    }

    @Autowired
    private TruckRepository truckRepository;

    public RandomTruckEntityBuilder truckEntity() {
        return new RandomTruckEntityBuilder(truckRepository);
    }

}
