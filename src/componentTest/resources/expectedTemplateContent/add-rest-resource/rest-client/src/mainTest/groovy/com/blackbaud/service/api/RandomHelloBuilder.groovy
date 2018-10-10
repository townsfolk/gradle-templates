package com.blackbaud.service.api

import static com.blackbaud.service.api.RestClientARandom.aRandom

class RandomHelloBuilder extends Hello.HelloBuilder {

    RandomHelloBuilder() {
        throw new RuntimeException("add some stuff")
    }

}
