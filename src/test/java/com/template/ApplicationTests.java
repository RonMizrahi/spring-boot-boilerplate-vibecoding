package com.template;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test for the main application class.
 * Verifies that the Spring application context loads successfully with all configured starters.
 */
@SpringBootTest
@ActiveProfiles("test")
class ApplicationTests {

    /**
     * Tests that the Spring application context loads without errors.
     * This test validates that all auto-configurations work together properly.
     */
    @Test
    void contextLoads() {
        // This test will pass if the Spring application context loads successfully
        // with all the configured starters. Uses Java 21 enhanced testing patterns.
    }
}
