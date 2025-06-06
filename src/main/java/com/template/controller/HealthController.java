package com.template.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    
    /**
     * Detailed health check endpoint for administrators.
     * 
     * @return detailed health status with system information
     */
    @GetMapping("/detailed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> detailedHealth() {
        // Using Java 21 enhanced Map.of() for immutable map creation
        var detailedResponse = Map.of(
            "status", "UP",
            "service", "microservice-template", 
            "timestamp", Instant.now().toString(),
            "version", "1.0.0-SNAPSHOT",
            "jvm", Map.of(
                "version", System.getProperty("java.version"),
                "vendor", System.getProperty("java.vendor"),
                "availableMemory", Runtime.getRuntime().maxMemory(),
                "freeMemory", Runtime.getRuntime().freeMemory(),
                "totalMemory", Runtime.getRuntime().totalMemory()
            ),
            "system", Map.of(
                "os", System.getProperty("os.name"),
                "arch", System.getProperty("os.arch"),
                "processors", Runtime.getRuntime().availableProcessors()
            )
        );
        
        return ResponseEntity.ok(detailedResponse);
    }
}
