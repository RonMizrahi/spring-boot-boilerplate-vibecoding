package com.template.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * JPA configuration for the application.
 * Enables JPA repositories and auditing for automatic timestamp handling.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.template.repository")
@EnableJpaAuditing
public class JpaConfig {
    
    // Additional JPA configuration can be added here as needed
    // For example: custom converters, validators, etc.
}
