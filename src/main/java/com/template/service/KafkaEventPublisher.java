package com.template.service;

import com.template.event.BaseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Service for publishing events to Kafka topics.
 * Provides methods for publishing various types of events.
 */
@Service
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers")
public class KafkaEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(KafkaEventPublisher.class);
    
    private final KafkaTemplate<String, Object> jsonKafkaTemplate;
    private final KafkaTemplate<String, String> stringKafkaTemplate;

    // Topic names
    public static final String USER_EVENTS_TOPIC = "user-events";
    public static final String NOTIFICATION_TOPIC = "notifications";
    public static final String AUDIT_TOPIC = "audit-events";

    public KafkaEventPublisher(KafkaTemplate<String, Object> jsonKafkaTemplate,
                              KafkaTemplate<String, String> stringKafkaTemplate) {
        this.jsonKafkaTemplate = jsonKafkaTemplate;
        this.stringKafkaTemplate = stringKafkaTemplate;
    }

    /**
     * Publish a JSON event to a topic.
     */
    public CompletableFuture<SendResult<String, Object>> publishEvent(String topic, String key, BaseEvent event) {
        logger.debug("Publishing event to topic '{}' with key '{}': {}", topic, key, event);
        
        try {
            CompletableFuture<SendResult<String, Object>> future = jsonKafkaTemplate.send(topic, key, event);
            
            future.whenComplete((result, exception) -> {
                if (exception == null) {
                    logger.info("Successfully published event to topic '{}' with key '{}' at offset {}",
                            topic, key, result.getRecordMetadata().offset());
                } else {
                    logger.error("Failed to publish event to topic '{}' with key '{}': {}", 
                            topic, key, exception.getMessage(), exception);
                }
            });
            
            return future;
        } catch (Exception e) {
            logger.error("Error publishing event to topic '{}' with key '{}': {}", topic, key, e.getMessage(), e);
            CompletableFuture<SendResult<String, Object>> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(e);
            return failedFuture;
        }
    }

    /**
     * Publish a JSON event to a topic without a key.
     */
    public CompletableFuture<SendResult<String, Object>> publishEvent(String topic, BaseEvent event) {
        return publishEvent(topic, null, event);
    }

    /**
     * Publish a string message to a topic.
     */
    public CompletableFuture<SendResult<String, String>> publishMessage(String topic, String key, String message) {
        logger.debug("Publishing message to topic '{}' with key '{}': {}", topic, key, message);
        
        try {
            CompletableFuture<SendResult<String, String>> future = stringKafkaTemplate.send(topic, key, message);
            
            future.whenComplete((result, exception) -> {
                if (exception == null) {
                    logger.info("Successfully published message to topic '{}' with key '{}' at offset {}",
                            topic, key, result.getRecordMetadata().offset());
                } else {
                    logger.error("Failed to publish message to topic '{}' with key '{}': {}", 
                            topic, key, exception.getMessage(), exception);
                }
            });
            
            return future;
        } catch (Exception e) {
            logger.error("Error publishing message to topic '{}' with key '{}': {}", topic, key, e.getMessage(), e);
            CompletableFuture<SendResult<String, String>> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(e);
            return failedFuture;
        }
    }

    /**
     * Publish a string message to a topic without a key.
     */
    public CompletableFuture<SendResult<String, String>> publishMessage(String topic, String message) {
        return publishMessage(topic, null, message);
    }

    /**
     * Publish user event to user-events topic.
     */
    public CompletableFuture<SendResult<String, Object>> publishUserEvent(BaseEvent event) {
        return publishEvent(USER_EVENTS_TOPIC, event);
    }

    /**
     * Publish notification message to notifications topic.
     */
    public CompletableFuture<SendResult<String, String>> publishNotification(String message) {
        return publishMessage(NOTIFICATION_TOPIC, message);
    }

    /**
     * Publish audit event to audit-events topic.
     */
    public CompletableFuture<SendResult<String, Object>> publishAuditEvent(BaseEvent event) {
        return publishEvent(AUDIT_TOPIC, event);
    }
}
