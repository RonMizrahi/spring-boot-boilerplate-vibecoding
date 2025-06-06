package com.template.repository.mongo;

import com.template.document.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for UserProfileRepository.
 * Uses Spring Data MongoDB test support for testing document operations.
 */
@DataMongoTest
@ActiveProfiles("test")
@DisplayName("UserProfileRepository Tests")
class UserProfileRepositoryTest {
    
    @Autowired
    private UserProfileRepository userProfileRepository;
    
    private UserProfile testProfile;
    
    @BeforeEach
    void setUp() {
        userProfileRepository.deleteAll();
        
        testProfile = new UserProfile();
        testProfile.setUserId(1L);
        testProfile.setBio("Software developer with passion for clean code");
        testProfile.setLocation("New York, NY");
        testProfile.setWebsite("https://johndoe.dev");
        testProfile.setPhoneNumber("+1-555-0123");
        testProfile.setDateOfBirth(LocalDate.of(1990, 5, 15));
        testProfile.setDefaults();
    }
    
    @Test
    @DisplayName("Should save and find user profile by ID")
    void shouldSaveAndFindUserProfileById() {
        // Given
        var savedProfile = userProfileRepository.save(testProfile);
        
        // When
        var foundProfile = userProfileRepository.findById(savedProfile.getId());
        
        // Then
        assertThat(foundProfile).isPresent();
        assertThat(foundProfile.get().getUserId()).isEqualTo(1L);
        assertThat(foundProfile.get().getBio()).isEqualTo("Software developer with passion for clean code");
    }
    
    @Test
    @DisplayName("Should find user profile by user ID")
    void shouldFindUserProfileByUserId() {
        // Given
        userProfileRepository.save(testProfile);
        
        // When
        var foundProfile = userProfileRepository.findByUserId(1L);
        
        // Then
        assertThat(foundProfile).isPresent();
        assertThat(foundProfile.get().getUserId()).isEqualTo(1L);
        assertThat(foundProfile.get().getBio()).isEqualTo("Software developer with passion for clean code");
    }
    
    @Test
    @DisplayName("Should check if user profile exists by user ID")
    void shouldCheckIfUserProfileExistsByUserId() {
        // Given
        userProfileRepository.save(testProfile);
        
        // When & Then
        assertThat(userProfileRepository.existsByUserId(1L)).isTrue();
        assertThat(userProfileRepository.existsByUserId(999L)).isFalse();
    }
    
    @Test
    @DisplayName("Should find user profiles by location ignoring case")
    void shouldFindUserProfilesByLocationIgnoringCase() {
        // Given
        testProfile.setLocation("New York, NY");
        userProfileRepository.save(testProfile);
        
        var anotherProfile = new UserProfile();
        anotherProfile.setUserId(2L);
        anotherProfile.setLocation("new york, ny");
        anotherProfile.setDefaults();
        userProfileRepository.save(anotherProfile);
        
        var pageable = PageRequest.of(0, 10);
        
        // When
        var profiles = userProfileRepository.findByLocationIgnoreCase("NEW YORK, NY", pageable);
        
        // Then
        assertThat(profiles.getContent()).hasSize(2);
        assertThat(profiles.getContent())
            .extracting(UserProfile::getUserId)
            .containsExactlyInAnyOrder(1L, 2L);
    }
    
    @Test
    @DisplayName("Should find user profiles by bio containing keywords ignoring case")
    void shouldFindUserProfilesByBioContainingKeywordsIgnoringCase() {
        // Given
        userProfileRepository.save(testProfile);
        
        var anotherProfile = new UserProfile();
        anotherProfile.setUserId(2L);
        anotherProfile.setBio("Frontend DEVELOPER specializing in React");
        anotherProfile.setDefaults();
        userProfileRepository.save(anotherProfile);
        
        var pageable = PageRequest.of(0, 10);
        
        // When
        var profiles = userProfileRepository.findByBioContainingIgnoreCase("developer", pageable);
        
        // Then
        assertThat(profiles.getContent()).hasSize(2);
        assertThat(profiles.getContent())
            .extracting(UserProfile::getUserId)
            .containsExactlyInAnyOrder(1L, 2L);
    }
    
    @Test
    @DisplayName("Should find user profiles by date of birth range")
    void shouldFindUserProfilesByDateOfBirthRange() {
        // Given
        testProfile.setDateOfBirth(LocalDate.of(1990, 5, 15));
        userProfileRepository.save(testProfile);
        
        var anotherProfile = new UserProfile();
        anotherProfile.setUserId(2L);
        anotherProfile.setDateOfBirth(LocalDate.of(1985, 3, 20));
        anotherProfile.setDefaults();
        userProfileRepository.save(anotherProfile);
        
        var thirdProfile = new UserProfile();
        thirdProfile.setUserId(3L);
        thirdProfile.setDateOfBirth(LocalDate.of(2000, 1, 1));
        thirdProfile.setDefaults();
        userProfileRepository.save(thirdProfile);
        
        // When
        var profiles = userProfileRepository.findByDateOfBirthBetween(
            LocalDate.of(1980, 1, 1), 
            LocalDate.of(1995, 12, 31)
        );
        
        // Then
        assertThat(profiles).hasSize(2);
        assertThat(profiles)
            .extracting(UserProfile::getUserId)
            .containsExactlyInAnyOrder(1L, 2L);
    }
    
    @Test
    @DisplayName("Should find user profiles created after specific date")
    void shouldFindUserProfilesCreatedAfterSpecificDate() {
        // Given
        var now = LocalDateTime.now();
        var profile = new UserProfile();
        profile.setUserId(1L);
        profile.setDefaults();
        userProfileRepository.save(profile);
        
        var pageable = PageRequest.of(0, 10);
        var yesterday = now.minusDays(1);
        
        // When
        var profiles = userProfileRepository.findByCreatedDateAfter(yesterday, pageable);
        
        // Then
        assertThat(profiles.getContent()).hasSize(1);
        assertThat(profiles.getContent().get(0).getUserId()).isEqualTo(1L);
    }
    
    @Test
    @DisplayName("Should find user profiles with website")
    void shouldFindUserProfilesWithWebsite() {
        // Given
        userProfileRepository.save(testProfile);
        
        var profileWithoutWebsite = new UserProfile();
        profileWithoutWebsite.setUserId(2L);
        profileWithoutWebsite.setDefaults();
        userProfileRepository.save(profileWithoutWebsite);
        
        var pageable = PageRequest.of(0, 10);
        
        // When
        var profiles = userProfileRepository.findByWebsiteIsNotNull(pageable);
        
        // Then
        assertThat(profiles.getContent()).hasSize(1);
        assertThat(profiles.getContent().get(0).getUserId()).isEqualTo(1L);
        assertThat(profiles.getContent().get(0).getWebsite()).isNotNull();
    }
    
    @Test
    @DisplayName("Should find user profiles with phone number")
    void shouldFindUserProfilesWithPhoneNumber() {
        // Given
        userProfileRepository.save(testProfile);
        
        var profileWithoutPhone = new UserProfile();
        profileWithoutPhone.setUserId(2L);
        profileWithoutPhone.setDefaults();
        userProfileRepository.save(profileWithoutPhone);
        
        var pageable = PageRequest.of(0, 10);
        
        // When
        var profiles = userProfileRepository.findByPhoneNumberIsNotNull(pageable);
        
        // Then
        assertThat(profiles.getContent()).hasSize(1);
        assertThat(profiles.getContent().get(0).getUserId()).isEqualTo(1L);
        assertThat(profiles.getContent().get(0).getPhoneNumber()).isNotNull();
    }
    
    @Test
    @DisplayName("Should find user profiles by privacy setting")
    void shouldFindUserProfilesByPrivacySetting() {
        // Given
        testProfile.setIsPublic(true);
        userProfileRepository.save(testProfile);
        
        var privateProfile = new UserProfile();
        privateProfile.setUserId(2L);
        privateProfile.setIsPublic(false);
        privateProfile.setDefaults();
        userProfileRepository.save(privateProfile);
        
        var pageable = PageRequest.of(0, 10);
        
        // When
        var publicProfiles = userProfileRepository.findByPrivacySetting(true, pageable);
        var privateProfiles = userProfileRepository.findByPrivacySetting(false, pageable);
        
        // Then
        assertThat(publicProfiles.getContent()).hasSize(1);
        assertThat(publicProfiles.getContent().get(0).getUserId()).isEqualTo(1L);
        
        assertThat(privateProfiles.getContent()).hasSize(1);
        assertThat(privateProfiles.getContent().get(0).getUserId()).isEqualTo(2L);
    }
    
    @Test
    @DisplayName("Should find user profiles by preference")
    void shouldFindUserProfilesByPreference() {
        // Given
        testProfile.addPreference("theme", "dark");
        userProfileRepository.save(testProfile);
        
        var anotherProfile = new UserProfile();
        anotherProfile.setUserId(2L);
        anotherProfile.setDefaults();
        anotherProfile.addPreference("theme", "light");
        userProfileRepository.save(anotherProfile);
        
        // When
        var darkThemeProfiles = userProfileRepository.findByPreference("theme", "dark");
        var lightThemeProfiles = userProfileRepository.findByPreference("theme", "light");
        
        // Then
        assertThat(darkThemeProfiles).hasSize(1);
        assertThat(darkThemeProfiles.get(0).getUserId()).isEqualTo(1L);
        
        assertThat(lightThemeProfiles).hasSize(1);
        assertThat(lightThemeProfiles.get(0).getUserId()).isEqualTo(2L);
    }
    
    @Test
    @DisplayName("Should count user profiles by location")
    void shouldCountUserProfilesByLocation() {
        // Given
        testProfile.setLocation("New York");
        userProfileRepository.save(testProfile);
        
        var anotherProfile = new UserProfile();
        anotherProfile.setUserId(2L);
        anotherProfile.setLocation("New York");
        anotherProfile.setDefaults();
        userProfileRepository.save(anotherProfile);
        
        var thirdProfile = new UserProfile();
        thirdProfile.setUserId(3L);
        thirdProfile.setLocation("San Francisco");
        thirdProfile.setDefaults();
        userProfileRepository.save(thirdProfile);
        
        // When
        var newYorkCount = userProfileRepository.countByLocation("New York");
        var sanFranciscoCount = userProfileRepository.countByLocation("San Francisco");
        
        // Then
        assertThat(newYorkCount).isEqualTo(2);
        assertThat(sanFranciscoCount).isEqualTo(1);
    }
    
    @Test
    @DisplayName("Should delete user profile by user ID")
    void shouldDeleteUserProfileByUserId() {
        // Given
        userProfileRepository.save(testProfile);
        assertThat(userProfileRepository.existsByUserId(1L)).isTrue();
        
        // When
        var deletedCount = userProfileRepository.deleteByUserId(1L);
        
        // Then
        assertThat(deletedCount).isEqualTo(1);
        assertThat(userProfileRepository.existsByUserId(1L)).isFalse();
    }
    
    @Test
    @DisplayName("Should find profiles with bio and location")
    void shouldFindProfilesWithBioAndLocation() {
        // Given
        testProfile.setBio("Developer");
        testProfile.setLocation("New York");
        userProfileRepository.save(testProfile);
        
        var profileWithoutBio = new UserProfile();
        profileWithoutBio.setUserId(2L);
        profileWithoutBio.setLocation("San Francisco");
        profileWithoutBio.setDefaults();
        userProfileRepository.save(profileWithoutBio);
        
        var profileWithoutLocation = new UserProfile();
        profileWithoutLocation.setUserId(3L);
        profileWithoutLocation.setBio("Designer");
        profileWithoutLocation.setDefaults();
        userProfileRepository.save(profileWithoutLocation);
        
        var pageable = PageRequest.of(0, 10);
        
        // When
        var completeProfiles = userProfileRepository.findProfilesWithBioAndLocation(pageable);
        
        // Then
        assertThat(completeProfiles.getContent()).hasSize(1);
        assertThat(completeProfiles.getContent().get(0).getUserId()).isEqualTo(1L);
    }
    
    @Test
    @DisplayName("Should find recently modified profiles")
    void shouldFindRecentlyModifiedProfiles() {
        // Given
        var profile = new UserProfile();
        profile.setUserId(1L);
        profile.setDefaults();
        userProfileRepository.save(profile);
        
        var pageable = PageRequest.of(0, 10);
        var yesterday = LocalDateTime.now().minusDays(1);
        
        // When
        var recentProfiles = userProfileRepository.findByLastModifiedDateAfter(yesterday, pageable);
        
        // Then
        assertThat(recentProfiles.getContent()).hasSize(1);
        assertThat(recentProfiles.getContent().get(0).getUserId()).isEqualTo(1L);
    }
}
