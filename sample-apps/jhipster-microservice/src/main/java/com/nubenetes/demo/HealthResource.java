package com.nubenetes.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class HealthResource {

    @GetMapping("/api/info")
    public Map<String, Object> getInfo() {
        return Map.of(
            "app", "jhipster-microservice",
            "version", "2.1.0",
            "runtime", "Java 21 / Spring Boot 3.3.4",
            "platform", "Red Hat OpenShift 4.20+"
        );
    }
}
