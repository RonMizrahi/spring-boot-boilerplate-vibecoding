package com.template.service;

import com.template.event.UserCreatedEvent;
import com.template.event.UserDeletedEvent;
import com.template.event.UserUpdatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for KafkaEventPublisher.
 */
@ExtendWith(MockitoExtension.class)
class KafkaEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> jsonKafkaTemplate;

    @Mock
    private KafkaTemplate<String, String> stringKafkaTemplate;

    @Mock
    private SendResult<String, Object> jsonSendResult;

    @Mock
    private SendResult<String, String> stringSendResult;

    private KafkaEventPublisher kafkaEventPublisher;

    @BeforeEach
    void setUp() {
        kafkaEventPublisher = new KafkaEventPublisher(jsonKafkaTemplate, stringKafkaTemplate);
    }    @Test
    void testPublishEvent_Success() {
        // Given
        UserCreatedEvent event = new UserCreatedEvent(
                "event-1", 
                LocalDateTime.now(), 
                "test-service", 
                1L, 
                "testuser", 
                "test@example.com"
        );

        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(jsonSendResult);
        when(jsonKafkaTemplate.send(anyString(), anyString(), any(Object.class))).thenReturn(future);

        // When
        CompletableFuture<SendResult<String, Object>> result = kafkaEventPublisher.publishEvent("test-topic", "test-key", event);

        // Then
        assertNotNull(result);
        assertTrue(result.isDone());
        verify(jsonKafkaTemplate).send("test-topic", "test-key", event);
    }    @Test
    void testPublishEvent_WithoutKey() {
        // Given
        UserUpdatedEvent event = new UserUpdatedEvent(
                "event-2", 
                LocalDateTime.now(), 
                "test-service", 
                1L, 
                "testuser", 
                "test@example.com"
        );

        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(jsonSendResult);
        when(jsonKafkaTemplate.send(anyString(), any(), any(Object.class))).thenReturn(future);

        // When
        CompletableFuture<SendResult<String, Object>> result = kafkaEventPublisher.publishEvent("test-topic", event);        // Then
        assertNotNull(result);
        assertTrue(result.isDone());
        verify(jsonKafkaTemplate).send(eq("test-topic"), any(), eq(event));
    }

    @Test
    void testPublishMessage_Success() {
        // Given
        String message = "Test notification message";
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(stringSendResult);
        when(stringKafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(future);

        // When
        CompletableFuture<SendResult<String, String>> result = kafkaEventPublisher.publishMessage("notifications", "key1", message);

        // Then
        assertNotNull(result);
        assertTrue(result.isDone());
        verify(stringKafkaTemplate).send("notifications", "key1", message);
    }

    @Test
    void testPublishMessage_WithoutKey() {
        // Given
        String message = "Test notification message";        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(stringSendResult);
        when(stringKafkaTemplate.send(anyString(), any(), anyString())).thenReturn(future);

        // When
        CompletableFuture<SendResult<String, String>> result = kafkaEventPublisher.publishMessage("notifications", message);

        // Then
        assertNotNull(result);
        assertTrue(result.isDone());
        verify(stringKafkaTemplate).send(eq("notifications"), any(), eq(message));
    }    @Test
    void testPublishUserEvent() {
        // Given
        UserCreatedEvent event = new UserCreatedEvent(
                "event-3", 
                LocalDateTime.now(), 
                "test-service", 
                1L, 
                "testuser", 
                "test@example.com"
        );

        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(jsonSendResult);
        when(jsonKafkaTemplate.send(anyString(), any(), any(Object.class))).thenReturn(future);

        // When
        CompletableFuture<SendResult<String, Object>> result = kafkaEventPublisher.publishUserEvent(event);

        // Then
        assertNotNull(result);
        assertTrue(result.isDone());
        verify(jsonKafkaTemplate).send(eq(KafkaEventPublisher.USER_EVENTS_TOPIC), any(), eq(event));
    }    @Test
    void testPublishNotification() {
        // Given
        String message = "Welcome notification";
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(stringSendResult);
        when(stringKafkaTemplate.send(anyString(), any(), anyString())).thenReturn(future);

        // When
        CompletableFuture<SendResult<String, String>> result = kafkaEventPublisher.publishNotification(message);

        // Then
        assertNotNull(result);
        assertTrue(result.isDone());
        verify(stringKafkaTemplate).send(eq(KafkaEventPublisher.NOTIFICATION_TOPIC), any(), eq(message));
    }    @Test
    void testPublishAuditEvent() {
        // Given
        UserDeletedEvent event = new UserDeletedEvent(
                "event-4", 
                LocalDateTime.now(), 
                "test-service", 
                1L, 
                "testuser"
        );

        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(jsonSendResult);
        when(jsonKafkaTemplate.send(anyString(), any(), any(Object.class))).thenReturn(future);

        // When
        CompletableFuture<SendResult<String, Object>> result = kafkaEventPublisher.publishAuditEvent(event);

        // Then
        assertNotNull(result);
        assertTrue(result.isDone());
        verify(jsonKafkaTemplate).send(eq(KafkaEventPublisher.AUDIT_TOPIC), any(), eq(event));
    }    @Test
    void testPublishEvent_Exception() {
        // Given
        UserCreatedEvent event = new UserCreatedEvent(
                "event-5", 
                LocalDateTime.now(), 
                "test-service", 
                1L, 
                "testuser", 
                "test@example.com"
        );

        when(jsonKafkaTemplate.send(anyString(), anyString(), any(Object.class)))
                .thenThrow(new RuntimeException("Kafka connection failed"));

        // When
        CompletableFuture<SendResult<String, Object>> result = kafkaEventPublisher.publishEvent("test-topic", "test-key", event);

        // Then
        assertNotNull(result);
        assertTrue(result.isCompletedExceptionally());
        verify(jsonKafkaTemplate).send("test-topic", "test-key", event);
    }
}
