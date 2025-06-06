package com.template.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Event published when a user is deleted.
 */
public class UserDeletedEvent extends BaseEvent {
    
    public static final String EVENT_TYPE = "USER_DELETED";
    
    private final Long userId;
    private final String username;

    @JsonCreator
    public UserDeletedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("timestamp") LocalDateTime timestamp,
            @JsonProperty("source") String source,
            @JsonProperty("userId") Long userId,
            @JsonProperty("username") String username) {
        super(eventId, EVENT_TYPE, timestamp, source);
        this.userId = userId;
        this.username = username;
    }

    public static UserDeletedEvent create(Long userId, String username) {
        return new UserDeletedEvent(
                java.util.UUID.randomUUID().toString(),
                LocalDateTime.now(),
                "user-service",
                userId,
                username
        );
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "UserDeletedEvent{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", " + super.toString() +
                '}';
    }
}
