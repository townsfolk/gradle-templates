package com.blackbaud.service.client;

import com.blackbaud.service.api.ResourcePaths;
import com.blackbaud.service.api.Hello;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.UUID;

@Headers({
        "Content-Type: application/json",
        "Accept: application/json",
})
public interface HelloClient {

}
