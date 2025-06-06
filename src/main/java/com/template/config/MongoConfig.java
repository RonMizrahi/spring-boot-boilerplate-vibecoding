package com.template.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoDB configuration for the application.
 * Enables MongoDB repositories and auditing for automatic timestamp handling.
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.template.repository.mongo")
@EnableMongoAuditing
public class MongoConfig {
    
    // Additional MongoDB configuration can be added here as needed
    // For example: custom converters, validators, connection settings, etc.
}
