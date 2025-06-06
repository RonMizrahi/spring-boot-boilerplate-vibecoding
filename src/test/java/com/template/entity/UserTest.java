package com.template.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for User entity.
 * Tests validation constraints, business logic, and Java 21 features.
 */
@DisplayName("User Entity Tests")
class UserTest {
    
    private Validator validator;
    
    @BeforeEach
    void setUp() {
        var factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    @DisplayName("Should create user with valid data")
    void shouldCreateUserWithValidData() {
        // Given
        var user = new User("testuser", "test@example.com", "password123");
        user.setFirstName("John");
        user.setLastName("Doe");
        
        // When & Then
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getEnabled()).isTrue();
        assertThat(user.getAccountNonExpired()).isTrue();
        assertThat(user.getAccountNonLocked()).isTrue();
        assertThat(user.getCredentialsNonExpired()).isTrue();
    }
    
    @Test
    @DisplayName("Should pass validation with valid user data")
    void shouldPassValidationWithValidUserData() {
        // Given
        var user = new User("testuser", "test@example.com", "password123");
        
        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Then
        assertThat(violations).isEmpty();
    }
    
    @Test
    @DisplayName("Should fail validation with blank username")
    void shouldFailValidationWithBlankUsername() {
        // Given
        var user = new User("", "test@example.com", "password123");
        
        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .contains("Username is required");
    }
    
    @Test
    @DisplayName("Should fail validation with short username")
    void shouldFailValidationWithShortUsername() {
        // Given
        var user = new User("ab", "test@example.com", "password123");
        
        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .contains("Username must be between 3 and 50 characters");
    }
    
    @Test
    @DisplayName("Should fail validation with long username")
    void shouldFailValidationWithLongUsername() {
        // Given
        var user = new User("a".repeat(51), "test@example.com", "password123");
        
        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .contains("Username must be between 3 and 50 characters");
    }
    
    @Test
    @DisplayName("Should fail validation with blank email")
    void shouldFailValidationWithBlankEmail() {
        // Given
        var user = new User("testuser", "", "password123");
        
        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .contains("Email is required");
    }
    
    @Test
    @DisplayName("Should fail validation with invalid email format")
    void shouldFailValidationWithInvalidEmailFormat() {
        // Given
        var user = new User("testuser", "invalid-email", "password123");
        
        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .contains("Email must be valid");
    }
    
    @Test
    @DisplayName("Should fail validation with short password")
    void shouldFailValidationWithShortPassword() {
        // Given
        var user = new User("testuser", "test@example.com", "short");
        
        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .contains("Password must be at least 8 characters");
    }
    
    @Test
    @DisplayName("Should return full name when both first and last names are present")
    void shouldReturnFullNameWhenBothNamesPresent() {
        // Given
        var user = new User("testuser", "test@example.com", "password123");
        user.setFirstName("John");
        user.setLastName("Doe");
        
        // When
        var fullName = user.getFullName();
        
        // Then
        assertThat(fullName).isEqualTo("John Doe");
    }
    
    @Test
    @DisplayName("Should return first name only when last name is null")
    void shouldReturnFirstNameOnlyWhenLastNameIsNull() {
        // Given
        var user = new User("testuser", "test@example.com", "password123");
        user.setFirstName("John");
        user.setLastName(null);
        
        // When
        var fullName = user.getFullName();
        
        // Then
        assertThat(fullName).isEqualTo("John");
    }
    
    @Test
    @DisplayName("Should return last name only when first name is null")
    void shouldReturnLastNameOnlyWhenFirstNameIsNull() {
        // Given
        var user = new User("testuser", "test@example.com", "password123");
        user.setFirstName(null);
        user.setLastName("Doe");
        
        // When
        var fullName = user.getFullName();
        
        // Then
        assertThat(fullName).isEqualTo("Doe");
    }
    
    @Test
    @DisplayName("Should return null when both names are null")
    void shouldReturnNullWhenBothNamesAreNull() {
        // Given
        var user = new User("testuser", "test@example.com", "password123");
        user.setFirstName(null);
        user.setLastName(null);
        
        // When
        var fullName = user.getFullName();
        
        // Then
        assertThat(fullName).isNull();
    }
    
    @Test
    @DisplayName("Should implement equals correctly")
    void shouldImplementEqualsCorrectly() {
        // Given
        var user1 = new User("testuser", "test@example.com", "password123");
        user1.setId(1L);
        
        var user2 = new User("testuser", "test@example.com", "password123");
        user2.setId(1L);
        
        var user3 = new User("otheruser", "other@example.com", "password123");
        user3.setId(2L);
        
        // When & Then
        assertThat(user1).isEqualTo(user2);
        assertThat(user1).isNotEqualTo(user3);
        assertThat(user1).isNotEqualTo(null);
        assertThat(user1).isNotEqualTo("string");
    }
    
    @Test
    @DisplayName("Should implement hashCode correctly")
    void shouldImplementHashCodeCorrectly() {
        // Given
        var user1 = new User("testuser", "test@example.com", "password123");
        user1.setId(1L);
        
        var user2 = new User("testuser", "test@example.com", "password123");
        user2.setId(1L);
        
        // When & Then
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }
    
    @Test
    @DisplayName("Should implement toString correctly using Java 21 STR template")
    void shouldImplementToStringCorrectly() {
        // Given
        var user = new User("testuser", "test@example.com", "password123");
        user.setId(1L);
        user.setCreatedAt(LocalDateTime.of(2024, 1, 1, 10, 0));
        
        // When
        var toString = user.toString();
        
        // Then
        assertThat(toString)
            .contains("User{")
            .contains("id=1")
            .contains("username='testuser'")
            .contains("email='test@example.com'")
            .contains("enabled=true")
            .contains("createdAt=2024-01-01T10:00");
    }
}
