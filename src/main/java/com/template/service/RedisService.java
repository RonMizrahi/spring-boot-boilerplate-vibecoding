package com.template.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis service for direct Redis operations.
 * Provides utility methods for caching and data storage.
 */
@Service
@ConditionalOnProperty(name = "spring.data.redis.host")
public class RedisService {

    private static final Logger logger = LoggerFactory.getLogger(RedisService.class);
    
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Set a key-value pair with expiration.
     */
    public void set(String key, Object value, Duration duration) {
        try {
            redisTemplate.opsForValue().set(key, value, duration);
            logger.debug("Set key '{}' with TTL {}", key, duration);
        } catch (Exception e) {
            logger.error("Error setting key '{}': {}", key, e.getMessage());
            throw new RuntimeException("Failed to set Redis key", e);
        }
    }

    /**
     * Set a key-value pair without expiration.
     */
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            logger.debug("Set key '{}'", key);
        } catch (Exception e) {
            logger.error("Error setting key '{}': {}", key, e.getMessage());
            throw new RuntimeException("Failed to set Redis key", e);
        }
    }

    /**
     * Get value by key.
     */
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            logger.error("Error getting key '{}': {}", key, e.getMessage());
            return null;
        }
    }

    /**
     * Delete a key.
     */
    public Boolean delete(String key) {
        try {
            Boolean result = redisTemplate.delete(key);
            logger.debug("Deleted key '{}': {}", key, result);
            return result;
        } catch (Exception e) {
            logger.error("Error deleting key '{}': {}", key, e.getMessage());
            return false;
        }
    }

    /**
     * Check if key exists.
     */
    public Boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            logger.error("Error checking key '{}': {}", key, e.getMessage());
            return false;
        }
    }

    /**
     * Set expiration for a key.
     */
    public Boolean expire(String key, Duration duration) {
        try {
            return redisTemplate.expire(key, duration);
        } catch (Exception e) {
            logger.error("Error setting expiration for key '{}': {}", key, e.getMessage());
            return false;
        }
    }

    /**
     * Get keys matching pattern.
     */
    public Set<String> keys(String pattern) {
        try {
            return redisTemplate.keys(pattern);
        } catch (Exception e) {
            logger.error("Error getting keys with pattern '{}': {}", pattern, e.getMessage());
            return Set.of();
        }
    }

    /**
     * Get time to live for a key.
     */
    public Long getExpire(String key) {
        try {
            return redisTemplate.getExpire(key, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("Error getting TTL for key '{}': {}", key, e.getMessage());
            return -1L;
        }
    }

    /**
     * Increment a numeric value.
     */
    public Long increment(String key) {
        try {
            return redisTemplate.opsForValue().increment(key);
        } catch (Exception e) {
            logger.error("Error incrementing key '{}': {}", key, e.getMessage());
            throw new RuntimeException("Failed to increment Redis key", e);
        }
    }

    /**
     * Increment a numeric value by delta.
     */
    public Long increment(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            logger.error("Error incrementing key '{}' by {}: {}", key, delta, e.getMessage());
            throw new RuntimeException("Failed to increment Redis key", e);
        }
    }

    /**
     * Add to a set.
     */
    public Long addToSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            logger.error("Error adding to set '{}': {}", key, e.getMessage());
            throw new RuntimeException("Failed to add to Redis set", e);
        }
    }

    /**
     * Get all members of a set.
     */
    public Set<Object> getSetMembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            logger.error("Error getting set members for '{}': {}", key, e.getMessage());
            return Set.of();
        }
    }

    /**
     * Push to a list (left push).
     */
    public Long pushToList(String key, Object value) {
        try {
            return redisTemplate.opsForList().leftPush(key, value);
        } catch (Exception e) {
            logger.error("Error pushing to list '{}': {}", key, e.getMessage());
            throw new RuntimeException("Failed to push to Redis list", e);
        }
    }

    /**
     * Pop from a list (right pop).
     */
    public Object popFromList(String key) {
        try {
            return redisTemplate.opsForList().rightPop(key);
        } catch (Exception e) {
            logger.error("Error popping from list '{}': {}", key, e.getMessage());
            return null;
        }
    }
}
