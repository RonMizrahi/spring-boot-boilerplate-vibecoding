package com.template.config;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Task Decorator that propagates the MDC context to async tasks.
 * Ensures that correlation IDs and other MDC values are available in async operations.
 */
@Component
public class MdcTaskDecorator implements TaskDecorator {    @Override
    @NonNull
    public Runnable decorate(@NonNull Runnable runnable) {
        // Capture the current MDC context
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        
        // Return a wrapped Runnable that sets up the MDC context before execution
        return () -> {
            try {
                // Set up the MDC context (if it was present)
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                // Execute the actual task
                runnable.run();
            } finally {
                // Always clear the context to prevent memory leaks
                MDC.clear();
            }
        };
    }
}
