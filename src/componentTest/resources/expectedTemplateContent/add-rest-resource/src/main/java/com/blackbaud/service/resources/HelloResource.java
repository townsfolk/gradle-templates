package com.blackbaud.service.resources;

import com.blackbaud.service.api.ResourcePaths;
import com.blackbaud.service.api.Hello;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = ResourcePaths.HELLO_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class HelloResource {

    @GetMapping("/{id}")
    public Hello find(@PathVariable("id") UUID id) {
        throw new IllegalStateException("implement");
    }


}
