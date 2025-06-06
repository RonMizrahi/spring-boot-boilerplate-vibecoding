package com.template.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for RedisService.
 * Uses mocked RedisTemplate to avoid requiring actual Redis instance.
 */
@SpringBootTest
@ActiveProfiles("test")
class RedisServiceTest {

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    @MockBean
    private ValueOperations<String, Object> valueOperations;

    @Test
    void testSetWithDuration() {
        // Given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        RedisService redisService = new RedisService(redisTemplate);

        // When
        redisService.set("test-key", "test-value", Duration.ofMinutes(10));

        // Then
        verify(valueOperations).set("test-key", "test-value", Duration.ofMinutes(10));
    }

    @Test
    void testSetWithoutDuration() {
        // Given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        RedisService redisService = new RedisService(redisTemplate);

        // When
        redisService.set("test-key", "test-value");

        // Then
        verify(valueOperations).set("test-key", "test-value");
    }

    @Test
    void testGet() {
        // Given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("test-key")).thenReturn("test-value");
        RedisService redisService = new RedisService(redisTemplate);

        // When
        Object result = redisService.get("test-key");

        // Then
        assertEquals("test-value", result);
        verify(valueOperations).get("test-key");
    }

    @Test
    void testDelete() {
        // Given
        when(redisTemplate.delete("test-key")).thenReturn(true);
        RedisService redisService = new RedisService(redisTemplate);

        // When
        Boolean result = redisService.delete("test-key");

        // Then
        assertTrue(result);
        verify(redisTemplate).delete("test-key");
    }

    @Test
    void testHasKey() {
        // Given
        when(redisTemplate.hasKey("test-key")).thenReturn(true);
        RedisService redisService = new RedisService(redisTemplate);

        // When
        Boolean result = redisService.hasKey("test-key");

        // Then
        assertTrue(result);
        verify(redisTemplate).hasKey("test-key");
    }

    @Test
    void testExpire() {
        // Given
        when(redisTemplate.expire("test-key", Duration.ofMinutes(5))).thenReturn(true);
        RedisService redisService = new RedisService(redisTemplate);

        // When
        Boolean result = redisService.expire("test-key", Duration.ofMinutes(5));

        // Then
        assertTrue(result);
        verify(redisTemplate).expire("test-key", Duration.ofMinutes(5));
    }

    @Test
    void testIncrement() {
        // Given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment("counter")).thenReturn(1L);
        RedisService redisService = new RedisService(redisTemplate);

        // When
        Long result = redisService.increment("counter");

        // Then
        assertEquals(1L, result);
        verify(valueOperations).increment("counter");
    }

    @Test
    void testIncrementWithDelta() {
        // Given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment("counter", 5L)).thenReturn(5L);
        RedisService redisService = new RedisService(redisTemplate);

        // When
        Long result = redisService.increment("counter", 5L);

        // Then
        assertEquals(5L, result);
        verify(valueOperations).increment("counter", 5L);
    }

    @Test
    void testHandleRedisException() {
        // Given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("error-key")).thenThrow(new RuntimeException("Redis error"));
        RedisService redisService = new RedisService(redisTemplate);

        // When
        Object result = redisService.get("error-key");

        // Then
        assertNull(result);
    }
}
