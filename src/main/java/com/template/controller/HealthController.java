package com.template.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Health check controller for service monitoring.
 * Provides simple health status endpoint for load balancers and monitoring tools
 * as well as detailed health information for administrators.
 */
@RestController
@RequestMapping("/health")
public class HealthController {
    
    private static final Logger log = LoggerFactory.getLogger(HealthController.class);
    
    private final Environment environment;
    private final Optional<BuildProperties> buildProperties;
    private final Optional<HealthEndpoint> healthEndpoint;
    
    public HealthController(
            Environment environment, 
            Optional<BuildProperties> buildProperties, 
            Optional<HealthEndpoint> healthEndpoint) {
        this.environment = environment;
        this.buildProperties = buildProperties;
        this.healthEndpoint = healthEndpoint;
    }
    
    /**
     * Simple health check endpoint.
     * Provides basic health status for load balancers and monitoring tools.
     * 
     * @return health status with timestamp
     */
    @GetMapping
    public ResponseEntity<Map<String, String>> health() {
        log.debug("Health check requested");
        // Using Java 21 enhanced Map.of() for immutable map creation
        var healthResponse = Map.of(
            "status", "UP",
            "service", "microservice-template", 
            "timestamp", Instant.now().toString(),
            "version", buildProperties.map(BuildProperties::getVersion).orElse("unknown")
        );
        return ResponseEntity.ok(healthResponse);
    }
    
    /**
     * Detailed health check endpoint for administrators.
     * Provides comprehensive system information and component health status.
     * 
     * @return detailed health status with system information
     */
    @GetMapping("/detailed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> detailedHealth() {
        log.info("Detailed health check requested by admin");
          // Create the response map
        var detailedResponse = new HashMap<String, Object>();
        detailedResponse.put("status", "UP");
        detailedResponse.put("service", environment.getProperty("spring.application.name", "microservice-template"));
        detailedResponse.put("timestamp", Instant.now().toString());
        detailedResponse.put("version", buildProperties.map(BuildProperties::getVersion).orElse("unknown"));
        detailedResponse.put("profiles", Arrays.toString(environment.getActiveProfiles()));
          // Add health components if available
        healthEndpoint.ifPresent(endpoint -> {
            detailedResponse.put("health", endpoint.health());
        });
        
        // JVM information
        var jvmInfo = new HashMap<String, Object>();
        jvmInfo.put("version", System.getProperty("java.version"));
        jvmInfo.put("vendor", System.getProperty("java.vendor"));
        jvmInfo.put("availableMemory", Runtime.getRuntime().maxMemory());
        jvmInfo.put("freeMemory", Runtime.getRuntime().freeMemory());
        jvmInfo.put("totalMemory", Runtime.getRuntime().totalMemory());
        detailedResponse.put("jvm", jvmInfo);
        
        // System information
        var systemInfo = new HashMap<String, Object>();
        systemInfo.put("os", System.getProperty("os.name"));
        systemInfo.put("arch", System.getProperty("os.arch"));
        systemInfo.put("processors", Runtime.getRuntime().availableProcessors());
        detailedResponse.put("system", systemInfo);
        
        return ResponseEntity.ok(detailedResponse);
    }
}
