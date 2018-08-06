package com.blackbaud.service;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@PersistenceTest
@ActiveProfiles({"local", "test", "componentTest"})
@SpringBootTest(
        classes = {ComponentTestConfig.class},
        properties = {"server.port=10000", "management.port=10001"},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
public @interface ComponentTest {

}
