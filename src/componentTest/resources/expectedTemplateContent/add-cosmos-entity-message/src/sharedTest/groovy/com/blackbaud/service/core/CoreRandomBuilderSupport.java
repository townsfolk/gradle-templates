package com.blackbaud.service.core;

import org.springframework.beans.factory.annotation.Autowired;
import com.blackbaud.service.core.domain.RandomCarEntityBuilder;
import com.blackbaud.service.core.domain.CarRepository;

public class CoreRandomBuilderSupport {

    @Autowired
    private CarRepository carRepository;

    public RandomCarEntityBuilder carEntity() {
        return new RandomCarEntityBuilder(carRepository);
    }

}
