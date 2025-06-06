package com.template.exception;

/**
 * Custom exceptions for the application.
 * Provides specific exception types for different error scenarios.
 */
public class CustomExceptions {

    /**
     * Exception thrown when a requested resource is not found.
     */
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
        
        public ResourceNotFoundException(String resource, String field, Object value) {
            super(String.format("%s not found with %s: %s", resource, field, value));
        }
    }

    /**
     * Exception thrown when a resource already exists (duplicate).
     */
    public static class ResourceAlreadyExistsException extends RuntimeException {
        public ResourceAlreadyExistsException(String message) {
            super(message);
        }
        
        public ResourceAlreadyExistsException(String resource, String field, Object value) {
            super(String.format("%s already exists with %s: %s", resource, field, value));
        }
    }

    /**
     * Exception thrown for invalid input or business logic violations.
     */
    public static class InvalidInputException extends RuntimeException {
        public InvalidInputException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown for unauthorized access attempts.
     */
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown for forbidden access attempts.
     */
    public static class ForbiddenException extends RuntimeException {
        public ForbiddenException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown for external service failures.
     */
    public static class ExternalServiceException extends RuntimeException {
        public ExternalServiceException(String message) {
            super(message);
        }
        
        public ExternalServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Exception thrown for data processing errors.
     */
    public static class DataProcessingException extends RuntimeException {
        public DataProcessingException(String message) {
            super(message);
        }
        
        public DataProcessingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
