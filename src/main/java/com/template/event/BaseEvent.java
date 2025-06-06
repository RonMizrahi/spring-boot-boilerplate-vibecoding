package com.template.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Base event class for all Kafka events.
 * Provides common fields and functionality.
 */
public abstract class BaseEvent {
    
    private final String eventId;
    private final String eventType;
    private final LocalDateTime timestamp;
    private final String source;

    @JsonCreator
    protected BaseEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("timestamp") LocalDateTime timestamp,
            @JsonProperty("source") String source) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.source = source;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "BaseEvent{" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", timestamp=" + timestamp +
                ", source='" + source + '\'' +
                '}';
    }
}
