package com.blackbaud.someservice.pact

import com.blackbaud.pact.BaseAuthPactProviderSpecification
import com.blackbaud.pact.api.InteractionDetails
import com.blackbaud.pact.support.ProviderPactInitializer
import com.blackbaud.pact.support.VerificationValidator
import com.blackbaud.testsupport.BBAuthSupport
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

@ComponentTest
class ProviderVerificationSpec extends BaseAuthPactProviderSpecification implements BBAuthSupport {

    @Autowired
    ProviderStateInitializer providerStateInitializer

    @Unroll
    def "#interactionDetails.type #interactionDetails.consumer.name #interactionDetails.interaction.description"() {
        given:
        this.providerPactExecutor.initialize(interactionDetails)

        and:
        initializeMocks(interactionDetails)

        when:
        VerificationValidator validator = this.providerPactExecutor.execute(interactionDetails)

        then:
        validator.validateResults()

        where:
        interactionDetails << pactInteractions
    }

    void initializeMocks(InteractionDetails interactionDetails) {
        // initialize any required mocks
    }

    @Override
    String getPactServiceName() {
        return 'some-service'
    }

    @Override
    ProviderPactInitializer getInitializer() {
        return this.providerStateInitializer
    }

    @Override
    String getLocalPath() {
        // return a fully qualified path to a pact .json file to run your test with those local interactions
        return null
    }

}
