package com.template.service;

import com.template.document.UserProfile;
import com.template.repository.mongo.UserProfileRepository;
import com.template.service.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserProfileService.
 * Uses Mockito for mocking dependencies and testing business logic.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserProfileService Tests")
class UserProfileServiceTest {
    
    @Mock
    private UserProfileRepository userProfileRepository;
    
    @InjectMocks
    private UserProfileService userProfileService;
    
    private UserProfile testProfile;
    
    @BeforeEach
    void setUp() {
        testProfile = new UserProfile();        testProfile.setId("profile123");
        testProfile.setUserId(1L);
        testProfile.setBio("Software developer with passion for clean code");
        testProfile.setLocation("New York, NY");
        testProfile.setWebsite("https://johndoe.dev");
        testProfile.setPhoneNumber("+1-555-0123");
        testProfile.setDateOfBirth(LocalDate.of(1990, 5, 15).atStartOfDay());
        testProfile.setIsPublic(true);
        testProfile.setUserPreferences(new java.util.HashMap<>());
    }
    
    @Test
    @DisplayName("Should create user profile successfully")
    void shouldCreateUserProfileSuccessfully() {
        // Given
        when(userProfileRepository.existsByUserId(1L)).thenReturn(false);
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(testProfile);
        
        // When
        var result = userProfileService.createUserProfile(testProfile);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getBio()).isEqualTo("Software developer with passion for clean code");
        
        verify(userProfileRepository).existsByUserId(1L);
        verify(userProfileRepository).save(testProfile);
    }
    
    @Test
    @DisplayName("Should throw exception when creating duplicate user profile")
    void shouldThrowExceptionWhenCreatingDuplicateUserProfile() {
        // Given
        when(userProfileRepository.existsByUserId(1L)).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> userProfileService.createUserProfile(testProfile))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User profile for user ID 1 already exists");
        
        verify(userProfileRepository).existsByUserId(1L);
        verify(userProfileRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should update user profile successfully")
    void shouldUpdateUserProfileSuccessfully() {
        // Given
        when(userProfileRepository.existsById("profile123")).thenReturn(true);
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(testProfile);
        
        // When
        var result = userProfileService.updateUserProfile(testProfile);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("profile123");
        
        verify(userProfileRepository).existsById("profile123");
        verify(userProfileRepository).save(testProfile);
    }
    
    @Test
    @DisplayName("Should throw exception when updating non-existent user profile")
    void shouldThrowExceptionWhenUpdatingNonExistentUserProfile() {
        // Given
        when(userProfileRepository.existsById("profile123")).thenReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> userProfileService.updateUserProfile(testProfile))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User profile with ID profile123 not found");
        
        verify(userProfileRepository).existsById("profile123");
        verify(userProfileRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should throw exception when updating user profile with null ID")
    void shouldThrowExceptionWhenUpdatingUserProfileWithNullId() {
        // Given
        testProfile.setId(null);
        
        // When & Then
        assertThatThrownBy(() -> userProfileService.updateUserProfile(testProfile))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User profile ID cannot be null for update");
        
        verify(userProfileRepository, never()).existsById(anyString());
        verify(userProfileRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should find user profile by ID")
    void shouldFindUserProfileById() {
        // Given
        when(userProfileRepository.findById("profile123")).thenReturn(Optional.of(testProfile));
        
        // When
        var result = userProfileService.findById("profile123");
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("profile123");
        
        verify(userProfileRepository).findById("profile123");
    }
    
    @Test
    @DisplayName("Should return empty when user profile not found by ID")
    void shouldReturnEmptyWhenUserProfileNotFoundById() {
        // Given
        when(userProfileRepository.findById("nonexistent")).thenReturn(Optional.empty());
        
        // When
        var result = userProfileService.findById("nonexistent");
        
        // Then
        assertThat(result).isEmpty();
        
        verify(userProfileRepository).findById("nonexistent");
    }
    
    @Test
    @DisplayName("Should find user profile by user ID")
    void shouldFindUserProfileByUserId() {
        // Given
        when(userProfileRepository.findByUserId(1L)).thenReturn(Optional.of(testProfile));
        
        // When
        var result = userProfileService.findByUserId(1L);
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo(1L);
        
        verify(userProfileRepository).findByUserId(1L);
    }
    
    @Test
    @DisplayName("Should find all user profiles with pagination")
    void shouldFindAllUserProfilesWithPagination() {
        // Given
        var pageable = PageRequest.of(0, 10);
        var profiles = List.of(testProfile);
        var page = new PageImpl<>(profiles, pageable, 1);
        
        when(userProfileRepository.findAll(pageable)).thenReturn(page);
        
        // When
        var result = userProfileService.findAll(pageable);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUserId()).isEqualTo(1L);
        
        verify(userProfileRepository).findAll(pageable);
    }
    
    @Test
    @DisplayName("Should find user profiles by location")
    void shouldFindUserProfilesByLocation() {
        // Given
        var pageable = PageRequest.of(0, 10);
        var profiles = List.of(testProfile);
        var page = new PageImpl<>(profiles, pageable, 1);
        
        when(userProfileRepository.findByLocationIgnoreCase("New York", pageable)).thenReturn(page);
        
        // When
        var result = userProfileService.findByLocation("New York", pageable);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getLocation()).isEqualTo("New York, NY");
        
        verify(userProfileRepository).findByLocationIgnoreCase("New York", pageable);
    }
    
    @Test
    @DisplayName("Should search user profiles by bio keywords")
    void shouldSearchUserProfilesByBioKeywords() {
        // Given
        var pageable = PageRequest.of(0, 10);
        var profiles = List.of(testProfile);
        var page = new PageImpl<>(profiles, pageable, 1);
        
        when(userProfileRepository.findByBioContainingIgnoreCase("developer", pageable)).thenReturn(page);
        
        // When
        var result = userProfileService.searchByBio("developer", pageable);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getBio()).contains("developer");
        
        verify(userProfileRepository).findByBioContainingIgnoreCase("developer", pageable);
    }
    
    @Test
    @DisplayName("Should find user profiles by age range")
    void shouldFindUserProfilesByAgeRange() {
        // Given
        var profiles = List.of(testProfile);
        when(userProfileRepository.findByDateOfBirthBetween(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(profiles);
        
        // When
        var result = userProfileService.findByAgeRange(30, 40);
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(1L);
        
        verify(userProfileRepository).findByDateOfBirthBetween(any(LocalDate.class), any(LocalDate.class));
    }
    
    @Test
    @DisplayName("Should find recently created user profiles")
    void shouldFindRecentlyCreatedUserProfiles() {
        // Given
        var pageable = PageRequest.of(0, 10);
        var profiles = List.of(testProfile);
        var page = new PageImpl<>(profiles, pageable, 1);
        
        when(userProfileRepository.findByCreatedDateAfter(any(LocalDateTime.class), eq(pageable)))
            .thenReturn(page);
        
        // When
        var result = userProfileService.findRecentlyCreated(7, pageable);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        
        verify(userProfileRepository).findByCreatedDateAfter(any(LocalDateTime.class), eq(pageable));
    }
    
    @Test
    @DisplayName("Should find user profiles with website")
    void shouldFindUserProfilesWithWebsite() {
        // Given
        var pageable = PageRequest.of(0, 10);
        var profiles = List.of(testProfile);
        var page = new PageImpl<>(profiles, pageable, 1);
        
        when(userProfileRepository.findByWebsiteIsNotNull(pageable)).thenReturn(page);
        
        // When
        var result = userProfileService.findWithWebsite(pageable);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getWebsite()).isNotNull();
        
        verify(userProfileRepository).findByWebsiteIsNotNull(pageable);
    }
    
    @Test
    @DisplayName("Should find user profiles with phone number")
    void shouldFindUserProfilesWithPhoneNumber() {
        // Given
        var pageable = PageRequest.of(0, 10);
        var profiles = List.of(testProfile);
        var page = new PageImpl<>(profiles, pageable, 1);
        
        when(userProfileRepository.findByPhoneNumberIsNotNull(pageable)).thenReturn(page);
        
        // When
        var result = userProfileService.findWithPhoneNumber(pageable);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getPhoneNumber()).isNotNull();
        
        verify(userProfileRepository).findByPhoneNumberIsNotNull(pageable);
    }
    
    @Test
    @DisplayName("Should find public user profiles")
    void shouldFindPublicUserProfiles() {
        // Given
        var pageable = PageRequest.of(0, 10);
        var profiles = List.of(testProfile);
        var page = new PageImpl<>(profiles, pageable, 1);
        
        when(userProfileRepository.findByPrivacySetting(true, pageable)).thenReturn(page);
        
        // When
        var result = userProfileService.findPublicProfiles(pageable);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        
        verify(userProfileRepository).findByPrivacySetting(true, pageable);
    }
    
    @Test
    @DisplayName("Should find private user profiles")
    void shouldFindPrivateUserProfiles() {
        // Given
        var pageable = PageRequest.of(0, 10);
        var profiles = List.of(testProfile);
        var page = new PageImpl<>(profiles, pageable, 1);
        
        when(userProfileRepository.findByPrivacySetting(false, pageable)).thenReturn(page);
        
        // When
        var result = userProfileService.findPrivateProfiles(pageable);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        
        verify(userProfileRepository).findByPrivacySetting(false, pageable);
    }
    
    @Test
    @DisplayName("Should find user profiles by preference")
    void shouldFindUserProfilesByPreference() {
        // Given
        var profiles = List.of(testProfile);
        when(userProfileRepository.findByPreference("theme", "dark")).thenReturn(profiles);
        
        // When
        var result = userProfileService.findByPreference("theme", "dark");
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(1L);
        
        verify(userProfileRepository).findByPreference("theme", "dark");
    }
    
    @Test
    @DisplayName("Should count user profiles by location")
    void shouldCountUserProfilesByLocation() {
        // Given
        when(userProfileRepository.countByLocation("New York")).thenReturn(5L);
        
        // When
        var result = userProfileService.countByLocation("New York");
        
        // Then
        assertThat(result).isEqualTo(5L);
        
        verify(userProfileRepository).countByLocation("New York");
    }
    
    @Test
    @DisplayName("Should find complete user profiles")
    void shouldFindCompleteUserProfiles() {
        // Given
        var pageable = PageRequest.of(0, 10);
        var profiles = List.of(testProfile);
        var page = new PageImpl<>(profiles, pageable, 1);
        
        when(userProfileRepository.findProfilesWithBioAndLocation(pageable)).thenReturn(page);
        
        // When
        var result = userProfileService.findCompleteProfiles(pageable);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        
        verify(userProfileRepository).findProfilesWithBioAndLocation(pageable);
    }
    
    @Test
    @DisplayName("Should find recently updated user profiles")
    void shouldFindRecentlyUpdatedUserProfiles() {
        // Given
        var pageable = PageRequest.of(0, 10);
        var profiles = List.of(testProfile);
        var page = new PageImpl<>(profiles, pageable, 1);
        
        when(userProfileRepository.findByLastModifiedDateAfter(any(LocalDateTime.class), eq(pageable)))
            .thenReturn(page);
        
        // When
        var result = userProfileService.findRecentlyUpdated(30, pageable);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        
        verify(userProfileRepository).findByLastModifiedDateAfter(any(LocalDateTime.class), eq(pageable));
    }
    
    @Test
    @DisplayName("Should delete user profile by ID successfully")
    void shouldDeleteUserProfileByIdSuccessfully() {
        // Given
        when(userProfileRepository.existsById("profile123")).thenReturn(true);
        doNothing().when(userProfileRepository).deleteById("profile123");
        
        // When
        userProfileService.deleteById("profile123");
        
        // Then
        verify(userProfileRepository).existsById("profile123");
        verify(userProfileRepository).deleteById("profile123");
    }
    
    @Test
    @DisplayName("Should throw exception when deleting non-existent user profile")
    void shouldThrowExceptionWhenDeletingNonExistentUserProfile() {
        // Given
        when(userProfileRepository.existsById("nonexistent")).thenReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> userProfileService.deleteById("nonexistent"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User profile with ID nonexistent not found");
        
        verify(userProfileRepository).existsById("nonexistent");
        verify(userProfileRepository, never()).deleteById(anyString());
    }
    
    @Test
    @DisplayName("Should delete user profile by user ID")
    void shouldDeleteUserProfileByUserId() {
        // Given
        when(userProfileRepository.deleteByUserId(1L)).thenReturn(1L);
        
        // When
        var result = userProfileService.deleteByUserId(1L);
        
        // Then
        assertThat(result).isEqualTo(1L);
        
        verify(userProfileRepository).deleteByUserId(1L);
    }
    
    @Test
    @DisplayName("Should check if user profile exists by user ID")
    void shouldCheckIfUserProfileExistsByUserId() {
        // Given
        when(userProfileRepository.existsByUserId(1L)).thenReturn(true);
        
        // When
        var result = userProfileService.existsByUserId(1L);
        
        // Then
        assertThat(result).isTrue();
        
        verify(userProfileRepository).existsByUserId(1L);
    }
    
    @Test
    @DisplayName("Should get total count of user profiles")
    void shouldGetTotalCountOfUserProfiles() {
        // Given
        when(userProfileRepository.count()).thenReturn(100L);
        
        // When
        var result = userProfileService.count();
        
        // Then
        assertThat(result).isEqualTo(100L);
        
        verify(userProfileRepository).count();
    }
}
