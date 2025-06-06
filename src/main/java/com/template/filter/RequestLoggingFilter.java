package com.template.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter to add correlation IDs to every request for tracing in logs.
 * Captures existing correlation IDs from headers or generates new ones.
 */
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
    
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String MDC_CORRELATION_ID_KEY = "correlationId";
    private static final String MDC_REQUEST_ID_KEY = "requestId";
      @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, 
            @NonNull HttpServletResponse response, 
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // Extract correlation ID from header or generate a new one
            String correlationId = request.getHeader(CORRELATION_ID_HEADER);
            if (correlationId == null || correlationId.isEmpty()) {
                correlationId = generateUniqueId();
            }
            
            // Always generate a new request ID for this specific request
            String requestId = generateUniqueId();
            
            // Add IDs to MDC for logging
            MDC.put(MDC_CORRELATION_ID_KEY, correlationId);
            MDC.put(MDC_REQUEST_ID_KEY, requestId);
            
            // Add IDs to response headers
            response.setHeader(CORRELATION_ID_HEADER, correlationId);
            response.setHeader(REQUEST_ID_HEADER, requestId);
            
            // Log request details
            if (log.isDebugEnabled()) {
                log.debug("Request received: {} {} [correlationId={}, requestId={}]", 
                        request.getMethod(), request.getRequestURI(), correlationId, requestId);
            }
            
            long startTime = System.currentTimeMillis();
            
            // Continue with the request
            filterChain.doFilter(request, response);
            
            // Log response details
            if (log.isDebugEnabled()) {
                long duration = System.currentTimeMillis() - startTime;
                log.debug("Response sent: {} {} - {} [{}ms] [correlationId={}, requestId={}]",
                        request.getMethod(), request.getRequestURI(), 
                        response.getStatus(), duration, correlationId, requestId);
            }
        } finally {
            // Always clear the MDC to prevent memory leaks
            MDC.remove(MDC_CORRELATION_ID_KEY);
            MDC.remove(MDC_REQUEST_ID_KEY);
        }
    }
    
    private String generateUniqueId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
