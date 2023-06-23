package com.honda.olympus.ms.transferfile.controller;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Operation;


@Controller
public class HealthCheckController {

    @Autowired
    private Environment environment;

    @Value("${service.name}")
    private String name;

    @Value("${service.version}")
    private String version;

    @Operation(summary = "Test service availability and version")
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
		String message = String.format("Honda Olympus [name: %s] [version: %s] [profile: %s] %s %s",
				name, version, Arrays.toString(environment.getActiveProfiles()), LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), TimeZone.getDefault().getID() );

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

}
