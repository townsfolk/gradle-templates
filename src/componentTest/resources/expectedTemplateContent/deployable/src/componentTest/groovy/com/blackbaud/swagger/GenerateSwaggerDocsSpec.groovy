package com.blackbaud.swagger

import com.blackbaud.service.ComponentTest
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

@ComponentTest
class GenerateSwaggerDocsSpec extends Specification {

    @Autowired
    SwaggerWriter swaggerWriter

    def "should write swagger.json file"() {
        expect:
        swaggerWriter.writeSwaggerJson()
    }

}
