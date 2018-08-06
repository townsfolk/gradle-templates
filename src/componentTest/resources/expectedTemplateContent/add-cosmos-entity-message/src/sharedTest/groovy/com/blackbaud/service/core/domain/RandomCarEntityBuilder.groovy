package com.blackbaud.service.core.domain

import static com.blackbaud.service.core.CoreARandom.aRandom

class RandomCarEntityBuilder extends CarEntity.CarEntityBuilder {

    private CarRepository carRepository

    RandomCarEntityBuilder(CarRepository carRepository) {
        this.carRepository = carRepository
        throw new RuntimeException("add some stuff")
    }

    CarEntity save() {
        carRepository.save(build())
    }

}
