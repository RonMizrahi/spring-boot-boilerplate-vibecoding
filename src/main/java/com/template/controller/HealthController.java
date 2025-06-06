package com.template.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Basic health check controller for service monitoring.
 * Provides simple health status endpoint for load balancers and monitoring tools.
 */
@RestController
@RequestMapping("/health")
public class HealthController {    /**
     * Simple health check endpoint.
     * 
     * @return health status with timestamp using Java 21 features
     */
    @GetMapping
    public ResponseEntity<Map<String, String>> health() {
        // Using Java 21 enhanced Map.of() for immutable map creation
        var healthResponse = Map.of(
            "status", "UP",
            "service", "microservice-template", 
            "timestamp", Instant.now().toString(),
            "version", "1.0.0-SNAPSHOT"
        );
        
        return ResponseEntity.ok(healthResponse);
    }
}
