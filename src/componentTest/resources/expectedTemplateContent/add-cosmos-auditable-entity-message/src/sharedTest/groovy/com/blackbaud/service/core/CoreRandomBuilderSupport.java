package com.blackbaud.service.core;

import org.springframework.beans.factory.annotation.Autowired;
import com.blackbaud.service.core.domain.RandomTruckEntityBuilder;
import com.blackbaud.service.core.domain.TruckRepository;

public class CoreRandomBuilderSupport {

    @Autowired
    private TruckRepository truckRepository;

    public RandomTruckEntityBuilder truckEntity() {
        return new RandomTruckEntityBuilder(truckRepository);
    }

}
