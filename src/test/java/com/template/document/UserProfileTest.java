package com.template.document;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for UserProfile document.
 * Tests validation constraints, business logic, and Java 21 features.
 */
@DisplayName("UserProfile Document Tests")
class UserProfileTest {
    
    private Validator validator;
    
    @BeforeEach
    void setUp() {
        var factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    @DisplayName("Should create user profile with valid data")
    void shouldCreateUserProfileWithValidData() {
        // Given
        var userProfile = new UserProfile();
        userProfile.setUserId(1L);
        userProfile.setBio("Software developer with passion for clean code");
        userProfile.setLocation("New York, NY");
        userProfile.setWebsite("https://johndoe.dev");
        userProfile.setPhoneNumber("+1-555-0123");
        userProfile.setDateOfBirth(LocalDate.of(1990, 5, 15));
        
        // When & Then
        assertThat(userProfile.getUserId()).isEqualTo(1L);
        assertThat(userProfile.getBio()).isEqualTo("Software developer with passion for clean code");
        assertThat(userProfile.getLocation()).isEqualTo("New York, NY");
        assertThat(userProfile.getWebsite()).isEqualTo("https://johndoe.dev");
        assertThat(userProfile.getPhoneNumber()).isEqualTo("+1-555-0123");
        assertThat(userProfile.getDateOfBirth()).isEqualTo(LocalDate.of(1990, 5, 15));
        assertThat(userProfile.getIsPublic()).isTrue(); // Default value
        assertThat(userProfile.getIsActive()).isTrue(); // Default value
        assertThat(userProfile.getPreferences()).isNotNull().isEmpty();
    }
    
    @Test
    @DisplayName("Should validate user ID is not null")
    void shouldValidateUserIdNotNull() {
        // Given
        var userProfile = new UserProfile();
        userProfile.setUserId(null);
        
        // When
        Set<ConstraintViolation<UserProfile>> violations = validator.validate(userProfile);
        
        // Then
        assertThat(violations).hasSize(1);
        var violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("userId");
        assertThat(violation.getMessage()).isEqualTo("User ID is required");
    }
    
    @Test
    @DisplayName("Should validate bio length constraint")
    void shouldValidateBioLength() {
        // Given
        var userProfile = new UserProfile();
        userProfile.setUserId(1L);
        userProfile.setBio("A".repeat(201)); // Exceeds 200 character limit
        
        // When
        Set<ConstraintViolation<UserProfile>> violations = validator.validate(userProfile);
        
        // Then
        assertThat(violations).hasSize(1);
        var violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("bio");
        assertThat(violation.getMessage()).isEqualTo("Bio must not exceed 200 characters");
    }
    
    @Test
    @DisplayName("Should validate location length constraint")
    void shouldValidateLocationLength() {
        // Given
        var userProfile = new UserProfile();
        userProfile.setUserId(1L);
        userProfile.setLocation("A".repeat(101)); // Exceeds 100 character limit
        
        // When
        Set<ConstraintViolation<UserProfile>> violations = validator.validate(userProfile);
        
        // Then
        assertThat(violations).hasSize(1);
        var violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("location");
        assertThat(violation.getMessage()).isEqualTo("Location must not exceed 100 characters");
    }
    
    @Test
    @DisplayName("Should validate website URL length constraint")
    void shouldValidateWebsiteLength() {
        // Given
        var userProfile = new UserProfile();
        userProfile.setUserId(1L);
        userProfile.setWebsite("https://" + "a".repeat(249)); // Exceeds 255 character limit
        
        // When
        Set<ConstraintViolation<UserProfile>> violations = validator.validate(userProfile);
        
        // Then
        assertThat(violations).hasSize(1);
        var violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("website");
        assertThat(violation.getMessage()).isEqualTo("Website URL must not exceed 255 characters");
    }
    
    @Test
    @DisplayName("Should validate phone number length constraint")
    void shouldValidatePhoneNumberLength() {
        // Given
        var userProfile = new UserProfile();
        userProfile.setUserId(1L);
        userProfile.setPhoneNumber("1".repeat(51)); // Exceeds 50 character limit
        
        // When
        Set<ConstraintViolation<UserProfile>> violations = validator.validate(userProfile);
        
        // Then
        assertThat(violations).hasSize(1);
        var violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("phoneNumber");
        assertThat(violation.getMessage()).isEqualTo("Phone number must not exceed 50 characters");
    }
    
    @Test
    @DisplayName("Should set default values correctly")
    void shouldSetDefaultValues() {
        // Given
        var userProfile = new UserProfile();
        userProfile.setUserId(1L);
        
        // When
        userProfile.setDefaults();
        
        // Then
        assertThat(userProfile.getIsPublic()).isTrue();
        assertThat(userProfile.getIsActive()).isTrue();
        assertThat(userProfile.getPreferences()).isNotNull().isEmpty();
    }
    
    @Test
    @DisplayName("Should handle preferences correctly")
    void shouldHandlePreferences() {
        // Given
        var userProfile = new UserProfile();
        userProfile.setUserId(1L);
        userProfile.setDefaults();
        
        // When
        userProfile.addPreference("theme", "dark");
        userProfile.addPreference("language", "en");
        userProfile.addPreference("notifications", "email");
        
        // Then
        assertThat(userProfile.getPreferences()).hasSize(3);
        assertThat(userProfile.getPreference("theme")).isEqualTo("dark");
        assertThat(userProfile.getPreference("language")).isEqualTo("en");
        assertThat(userProfile.getPreference("notifications")).isEqualTo("email");
        assertThat(userProfile.hasPreference("theme")).isTrue();
        assertThat(userProfile.hasPreference("nonexistent")).isFalse();
    }
    
    @Test
    @DisplayName("Should remove preferences correctly")
    void shouldRemovePreferences() {
        // Given
        var userProfile = new UserProfile();
        userProfile.setUserId(1L);
        userProfile.setDefaults();
        userProfile.addPreference("theme", "dark");
        userProfile.addPreference("language", "en");
        
        // When
        userProfile.removePreference("theme");
        
        // Then
        assertThat(userProfile.getPreferences()).hasSize(1);
        assertThat(userProfile.hasPreference("theme")).isFalse();
        assertThat(userProfile.hasPreference("language")).isTrue();
    }
    
    @Test
    @DisplayName("Should clear preferences correctly")
    void shouldClearPreferences() {
        // Given
        var userProfile = new UserProfile();
        userProfile.setUserId(1L);
        userProfile.setDefaults();
        userProfile.addPreference("theme", "dark");
        userProfile.addPreference("language", "en");
        
        // When
        userProfile.clearPreferences();
        
        // Then
        assertThat(userProfile.getPreferences()).isEmpty();
    }
    
    @Test
    @DisplayName("Should calculate age correctly")
    void shouldCalculateAge() {
        // Given
        var userProfile = new UserProfile();
        userProfile.setUserId(1L);
        userProfile.setDateOfBirth(LocalDate.now().minusYears(30));
        
        // When
        var age = userProfile.calculateAge();
        
        // Then
        assertThat(age).isEqualTo(30);
    }
    
    @Test
    @DisplayName("Should return null age when date of birth is null")
    void shouldReturnNullAgeWhenDateOfBirthIsNull() {
        // Given
        var userProfile = new UserProfile();
        userProfile.setUserId(1L);
        userProfile.setDateOfBirth(null);
        
        // When
        var age = userProfile.calculateAge();
        
        // Then
        assertThat(age).isNull();
    }
    
    @Test
    @DisplayName("Should check if profile is complete")
    void shouldCheckIfProfileIsComplete() {
        // Given
        var incompleteProfile = new UserProfile();
        incompleteProfile.setUserId(1L);
        
        var completeProfile = new UserProfile();
        completeProfile.setUserId(2L);
        completeProfile.setBio("Software developer");
        completeProfile.setLocation("New York");
        completeProfile.setDateOfBirth(LocalDate.of(1990, 1, 1));
        
        // When & Then
        assertThat(incompleteProfile.isProfileComplete()).isFalse();
        assertThat(completeProfile.isProfileComplete()).isTrue();
    }
    
    @Test
    @DisplayName("Should implement equals and hashCode correctly")
    void shouldImplementEqualsAndHashCodeCorrectly() {
        // Given
        var profile1 = new UserProfile();
        profile1.setId("profile1");
        profile1.setUserId(1L);
        profile1.setBio("Test bio");
        
        var profile2 = new UserProfile();
        profile2.setId("profile1");
        profile2.setUserId(1L);
        profile2.setBio("Test bio");
        
        var profile3 = new UserProfile();
        profile3.setId("profile2");
        profile3.setUserId(2L);
        profile3.setBio("Different bio");
        
        // When & Then
        assertThat(profile1).isEqualTo(profile2);
        assertThat(profile1).isNotEqualTo(profile3);
        assertThat(profile1.hashCode()).isEqualTo(profile2.hashCode());
        assertThat(profile1.hashCode()).isNotEqualTo(profile3.hashCode());
    }
    
    @Test
    @DisplayName("Should implement toString correctly")
    void shouldImplementToStringCorrectly() {
        // Given
        var userProfile = new UserProfile();
        userProfile.setId("profile123");
        userProfile.setUserId(1L);
        userProfile.setBio("Software developer");
        userProfile.setLocation("New York");
        
        // When
        var toString = userProfile.toString();
        
        // Then
        assertThat(toString).contains("UserProfile");
        assertThat(toString).contains("id=profile123");
        assertThat(toString).contains("userId=1");
        assertThat(toString).contains("bio=Software developer");
        assertThat(toString).contains("location=New York");
    }
    
    @Test
    @DisplayName("Should handle null values gracefully in utility methods")
    void shouldHandleNullValuesGracefully() {
        // Given
        var userProfile = new UserProfile();
        userProfile.setUserId(1L);
        
        // When & Then
        assertThat(userProfile.getPreference("nonexistent")).isNull();
        assertThat(userProfile.hasPreference("nonexistent")).isFalse();
        assertThat(userProfile.calculateAge()).isNull();
        assertThat(userProfile.isProfileComplete()).isFalse();
    }
}
