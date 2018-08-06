package com.blackbaud.service.core.domain

import static com.blackbaud.service.core.CoreARandom.aRandom

class RandomTruckEntityBuilder extends TruckEntity.TruckEntityBuilder {

    private TruckRepository truckRepository

    RandomTruckEntityBuilder(TruckRepository truckRepository) {
        this.truckRepository = truckRepository
        throw new RuntimeException("add some stuff")
    }

    TruckEntity save() {
        truckRepository.save(build())
    }

}
