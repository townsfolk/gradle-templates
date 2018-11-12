package com.blackbaud.service.pacttest

import au.com.dius.pact.consumer.PactVerificationResult
import au.com.dius.pact.consumer.groovy.PactBuilder
import com.blackbaud.service.api.Foo
import com.blackbaud.service.core.domain.foo.GetFooClient
import com.blackbaud.service.core.domain.foo.GetFooClientSasProperties
import com.blackbaud.sasquatch.FakeSASClient
import com.blackbaud.sasquatch.TestSasquatchConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(classes = [GetFooClientConfig, TestSasquatchConfiguration], webEnvironment = SpringBootTest.WebEnvironment.NONE)
class FooPactSpec extends Specification {
    @Autowired
    GetFooClient getFooClient

    @Autowired
    FakeSASClient fakeSASClient

    @Value('${sas.foo.pod}')
    String fooSasPod

    int pactPort = 8000

    PactBuilder fooPact = new PactBuilder()

    def setup() {
        fakeSASClient.useLocalhostForAudience(FooSasPod, GetFooClientSasProperties.FOO_SERVICE_TYPE, pactPort)
        FooPact.with {
            serviceConsumer "Service"
            hasPactWith "foo"
            port pactPort
        }
    }

    def "Service can get Foo"() {
        given:
        UUID id = UUID.randomUUID()
        fooPact.with {
            uponReceiving "Service can get Foo"
            withAttributes(
                    method: "GET",
                    path: GetFooClient.FOO_PATH,
                    query: "id=${id}".toString(),
                    headers: [
                            "Authorization": "Bearer mock_access_token_sas",
                            "Accept" : "application/json"
                    ]
            )
            given("requires pre-existing data")
            willRespondWith(status: 200)
            withBody
                    {
                        "foo": "${id}"
                    }
        }

        when:
        PactVerificationResult result = fooPact.runTest {
            Foo result = getFooClient.getFoo(id)
            assert result.id = id
        }

        then:
        assert result == PactVerificationResult.Ok.INSTANCE
    }
}
