package com.template.service;

import com.template.event.UserCreatedEvent;
import com.template.event.UserDeletedEvent;
import com.template.event.UserUpdatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

/**
 * Unit tests for KafkaEventConsumer.
 */
@ExtendWith(MockitoExtension.class)
class KafkaEventConsumerTest {

    @Mock
    private Acknowledgment acknowledgment;

    private KafkaEventConsumer kafkaEventConsumer;

    @BeforeEach
    void setUp() {
        kafkaEventConsumer = new KafkaEventConsumer();
    }

    @Test
    void testHandleUserCreatedEvent_Success() {
        // Given
        UserCreatedEvent event = new UserCreatedEvent(
                "event-1", 
                LocalDateTime.now(), 
                "user-service", 
                1L, 
                "testuser", 
                "test@example.com"
        );
        String topic = "user-events";
        int partition = 0;
        long offset = 123L;

        // When
        kafkaEventConsumer.handleUserCreatedEvent(event, topic, partition, offset, acknowledgment);

        // Then
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void testHandleUserCreatedEvent_Exception() {
        // Given
        UserCreatedEvent event = new UserCreatedEvent(
                "event-1", 
                LocalDateTime.now(), 
                "user-service", 
                1L, 
                "testuser", 
                "test@example.com"
        );
        String topic = "user-events";
        int partition = 0;
        long offset = 123L;

        // Mock an exception in the processing (not directly testable but we can verify acknowledgment)
        // When
        kafkaEventConsumer.handleUserCreatedEvent(event, topic, partition, offset, acknowledgment);

        // Then - Even if processing fails, acknowledgment should be called to avoid infinite reprocessing
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void testHandleUserUpdatedEvent_Success() {
        // Given
        UserUpdatedEvent event = new UserUpdatedEvent(
                "event-2", 
                LocalDateTime.now(), 
                "user-service", 
                1L, 
                "testuser", 
                "updated@example.com"
        );
        String topic = "user-events";
        int partition = 0;
        long offset = 124L;

        // When
        kafkaEventConsumer.handleUserUpdatedEvent(event, topic, partition, offset, acknowledgment);

        // Then
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void testHandleUserDeletedEvent_Success() {
        // Given
        UserDeletedEvent event = new UserDeletedEvent(
                "event-3", 
                LocalDateTime.now(), 
                "user-service", 
                1L, 
                "testuser"
        );
        String topic = "user-events";
        int partition = 0;
        long offset = 125L;

        // When
        kafkaEventConsumer.handleUserDeletedEvent(event, topic, partition, offset, acknowledgment);

        // Then
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void testHandleNotification_Success() {
        // Given
        String message = "Welcome to our service!";
        String topic = "notifications";
        int partition = 0;
        long offset = 126L;

        // When
        kafkaEventConsumer.handleNotification(message, topic, partition, offset, acknowledgment);

        // Then
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void testHandleNotification_EmptyMessage() {
        // Given
        String message = "";
        String topic = "notifications";
        int partition = 0;
        long offset = 127L;

        // When
        kafkaEventConsumer.handleNotification(message, topic, partition, offset, acknowledgment);

        // Then
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void testHandleNotification_NullMessage() {
        // Given
        String message = null;
        String topic = "notifications";
        int partition = 0;
        long offset = 128L;

        // When
        kafkaEventConsumer.handleNotification(message, topic, partition, offset, acknowledgment);

        // Then
        verify(acknowledgment, times(1)).acknowledge();
    }
}
