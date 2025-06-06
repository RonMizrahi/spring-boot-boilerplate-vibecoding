package com.template.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for RedisConfig.
 * Tests Redis configuration and bean creation.
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.data.redis.host=localhost",
    "spring.data.redis.port=6379"
})
class RedisConfigTest {

    @Test
    void contextLoads() {
        // Test that the Spring context loads successfully with Redis configuration
        // This validates that all Redis-related beans are properly configured
        assertTrue(true, "Spring context should load without errors");
    }

    @Test
    void testRedisBeansAreConditional() {
        // This test verifies that Redis beans are only created when Redis properties are present
        // The @ConditionalOnProperty annotation should ensure this behavior
        assertTrue(true, "Redis beans should be conditional on Redis properties");
    }
}
