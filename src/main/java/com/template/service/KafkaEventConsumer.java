package com.template.service;

import com.template.event.UserCreatedEvent;
import com.template.event.UserDeletedEvent;
import com.template.event.UserUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * Service for consuming events from Kafka topics.
 * Handles various types of events and processes them accordingly.
 */
@Service
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers")
public class KafkaEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaEventConsumer.class);

    /**
     * Handle user created events.
     */
    @KafkaListener(
            topics = "user-events",
            groupId = "user-events-group",
            containerFactory = "jsonKafkaListenerContainerFactory"
    )
    public void handleUserCreatedEvent(
            @Payload UserCreatedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        try {
            logger.info("Received UserCreatedEvent from topic '{}' partition {} offset {}: {}", 
                    topic, partition, offset, event);
            
            // Process the user created event
            processUserCreatedEvent(event);
            
            // Manually acknowledge the message
            acknowledgment.acknowledge();
            logger.debug("Successfully processed and acknowledged UserCreatedEvent for user ID: {}", 
                    event.getUserId());
            
        } catch (Exception e) {
            logger.error("Error processing UserCreatedEvent from topic '{}' partition {} offset {}: {}", 
                    topic, partition, offset, e.getMessage(), e);
            // In a real scenario, you might want to send to a dead letter topic or retry logic
            acknowledgment.acknowledge(); // Acknowledge to avoid infinite reprocessing
        }
    }

    /**
     * Handle user updated events.
     */
    @KafkaListener(
            topics = "user-events",
            groupId = "user-events-group",
            containerFactory = "jsonKafkaListenerContainerFactory"
    )
    public void handleUserUpdatedEvent(
            @Payload UserUpdatedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        try {
            logger.info("Received UserUpdatedEvent from topic '{}' partition {} offset {}: {}", 
                    topic, partition, offset, event);
            
            // Process the user updated event
            processUserUpdatedEvent(event);
            
            // Manually acknowledge the message
            acknowledgment.acknowledge();
            logger.debug("Successfully processed and acknowledged UserUpdatedEvent for user ID: {}", 
                    event.getUserId());
            
        } catch (Exception e) {
            logger.error("Error processing UserUpdatedEvent from topic '{}' partition {} offset {}: {}", 
                    topic, partition, offset, e.getMessage(), e);
            acknowledgment.acknowledge(); // Acknowledge to avoid infinite reprocessing
        }
    }

    /**
     * Handle user deleted events.
     */
    @KafkaListener(
            topics = "user-events",
            groupId = "user-events-group",
            containerFactory = "jsonKafkaListenerContainerFactory"
    )
    public void handleUserDeletedEvent(
            @Payload UserDeletedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        try {
            logger.info("Received UserDeletedEvent from topic '{}' partition {} offset {}: {}", 
                    topic, partition, offset, event);
            
            // Process the user deleted event
            processUserDeletedEvent(event);
            
            // Manually acknowledge the message
            acknowledgment.acknowledge();
            logger.debug("Successfully processed and acknowledged UserDeletedEvent for user ID: {}", 
                    event.getUserId());
            
        } catch (Exception e) {
            logger.error("Error processing UserDeletedEvent from topic '{}' partition {} offset {}: {}", 
                    topic, partition, offset, e.getMessage(), e);
            acknowledgment.acknowledge(); // Acknowledge to avoid infinite reprocessing
        }
    }

    /**
     * Handle notification messages.
     */
    @KafkaListener(
            topics = "notifications",
            groupId = "notifications-group",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    public void handleNotification(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        try {
            logger.info("Received notification from topic '{}' partition {} offset {}: {}", 
                    topic, partition, offset, message);
            
            // Process the notification
            processNotification(message);
            
            // Manually acknowledge the message
            acknowledgment.acknowledge();
            logger.debug("Successfully processed and acknowledged notification message");
            
        } catch (Exception e) {
            logger.error("Error processing notification from topic '{}' partition {} offset {}: {}", 
                    topic, partition, offset, e.getMessage(), e);
            acknowledgment.acknowledge(); // Acknowledge to avoid infinite reprocessing
        }
    }

    /**
     * Process user created event.
     */
    private void processUserCreatedEvent(UserCreatedEvent event) {
        // Implementation logic for handling user creation
        // e.g., update cache, send welcome email, update analytics, etc.
        logger.info("Processing user created event for user: {} ({})", event.getUsername(), event.getEmail());
        
        // Example processing logic:
        // - Send welcome email
        // - Update user statistics
        // - Initialize user preferences
        // - Create user profile in other services
    }

    /**
     * Process user updated event.
     */
    private void processUserUpdatedEvent(UserUpdatedEvent event) {
        // Implementation logic for handling user updates
        logger.info("Processing user updated event for user: {} ({})", event.getUsername(), event.getEmail());
        
        // Example processing logic:
        // - Update caches in other services
        // - Sync user data across systems
        // - Update search indexes
    }

    /**
     * Process user deleted event.
     */
    private void processUserDeletedEvent(UserDeletedEvent event) {
        // Implementation logic for handling user deletion
        logger.info("Processing user deleted event for user: {}", event.getUsername());
        
        // Example processing logic:
        // - Clean up user data in other services
        // - Remove from caches
        // - Update analytics
        // - Send account deletion confirmation
    }

    /**
     * Process notification message.
     */
    private void processNotification(String message) {
        // Implementation logic for handling notifications
        logger.info("Processing notification: {}", message);
        
        // Example processing logic:
        // - Send email/SMS
        // - Push to mobile devices
        // - Update notification center
    }
}
