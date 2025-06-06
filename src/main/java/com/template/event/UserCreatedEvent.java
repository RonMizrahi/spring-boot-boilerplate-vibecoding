package com.template.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Event published when a user is created.
 */
public class UserCreatedEvent extends BaseEvent {
    
    public static final String EVENT_TYPE = "USER_CREATED";
    
    private final Long userId;
    private final String username;
    private final String email;

    @JsonCreator
    public UserCreatedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("timestamp") LocalDateTime timestamp,
            @JsonProperty("source") String source,
            @JsonProperty("userId") Long userId,
            @JsonProperty("username") String username,
            @JsonProperty("email") String email) {
        super(eventId, EVENT_TYPE, timestamp, source);
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

    public static UserCreatedEvent create(Long userId, String username, String email) {
        return new UserCreatedEvent(
                java.util.UUID.randomUUID().toString(),
                LocalDateTime.now(),
                "user-service",
                userId,
                username,
                email
        );
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "UserCreatedEvent{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", " + super.toString() +
                '}';
    }
}
