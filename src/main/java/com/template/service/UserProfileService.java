package com.template.service;

import com.template.document.UserProfile;
import com.template.repository.mongo.UserProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for UserProfile document operations.
 * Provides business logic layer between controllers and repository.
 * Uses Java 21 features and Spring best practices.
 */
@Service
public class UserProfileService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class);
    
    private final UserProfileRepository userProfileRepository;
    
    /**
     * Constructor injection for UserProfileRepository.
     * 
     * @param userProfileRepository the user profile repository
     */
    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }
    
    /**
     * Creates a new user profile.
     *     * @param userProfile the user profile to create
     * @return the created user profile
     * @throws IllegalArgumentException if user profile data is invalid
     */
    @CachePut(value = "userProfiles", key = "#result.id")
    public UserProfile createUserProfile(UserProfile userProfile) {
        logger.debug("Creating new user profile for user ID: {}", userProfile.getUserId());
        
        // Validate unique constraint
        if (userProfileRepository.existsByUserId(userProfile.getUserId())) {
            throw new IllegalArgumentException("User profile for user ID " + userProfile.getUserId() + " already exists");
        }
        
        var savedProfile = userProfileRepository.save(userProfile);
        logger.info("Created user profile with ID: {} for user ID: {}", savedProfile.getId(), savedProfile.getUserId());
        
        return savedProfile;
    }
    
    /**
     * Updates an existing user profile.
     * 
     * @param userProfile the user profile to update     * @return the updated user profile
     * @throws IllegalArgumentException if user profile doesn't exist or data is invalid
     */
    @CachePut(value = "userProfiles", key = "#userProfile.id")
    @CacheEvict(value = "userProfiles", key = "'userId:' + #userProfile.userId")
    public UserProfile updateUserProfile(UserProfile userProfile) {
        logger.debug("Updating user profile with ID: {}", userProfile.getId());
        
        if (userProfile.getId() == null) {
            throw new IllegalArgumentException("User profile ID cannot be null for update");
        }
        
        if (!userProfileRepository.existsById(userProfile.getId())) {
            throw new IllegalArgumentException("User profile with ID " + userProfile.getId() + " not found");
        }
        
        var updatedProfile = userProfileRepository.save(userProfile);
        logger.info("Updated user profile with ID: {}", updatedProfile.getId());
        
        return updatedProfile;
    }
    
    /**
     * Finds a user profile by ID.
     *     * @param id the user profile ID
     * @return Optional containing the user profile if found
     */
    @Cacheable(value = "userProfiles", key = "#id")
    public Optional<UserProfile> findById(String id) {
        logger.debug("Finding user profile by ID: {}", id);
        return userProfileRepository.findById(id);
    }
    
    /**
     * Finds a user profile by user ID.
     * 
     * @param userId the user ID
     * @return Optional containing the user profile if found
     */
    @Cacheable(value = "userProfiles", key = "'userId:' + #userId")
    public Optional<UserProfile> findByUserId(Long userId) {
        logger.debug("Finding user profile by user ID: {}", userId);
        return userProfileRepository.findByUserId(userId);
    }
    
    /**
     * Gets all user profiles with pagination.
     * 
     * @param pageable pagination information
     * @return Page of user profiles
     */
    public Page<UserProfile> findAll(Pageable pageable) {
        logger.debug("Finding all user profiles with pagination");
        return userProfileRepository.findAll(pageable);
    }
    
    /**
     * Finds user profiles by location.
     * 
     * @param location the location to search for
     * @param pageable pagination information
     * @return Page of user profiles
     */
    public Page<UserProfile> findByLocation(String location, Pageable pageable) {
        logger.debug("Finding user profiles by location: {}", location);
        return userProfileRepository.findByLocationIgnoreCase(location, pageable);
    }
    
    /**
     * Searches user profiles by bio keywords.
     * 
     * @param keywords the keywords to search in bio
     * @param pageable pagination information
     * @return Page of user profiles
     */
    public Page<UserProfile> searchByBio(String keywords, Pageable pageable) {
        logger.debug("Searching user profiles by bio keywords: {}", keywords);
        return userProfileRepository.findByBioContainingIgnoreCase(keywords, pageable);
    }
    
    /**
     * Finds user profiles by age range (calculated from date of birth).
     * 
     * @param minAge minimum age
     * @param maxAge maximum age
     * @return List of user profiles
     */
    public List<UserProfile> findByAgeRange(int minAge, int maxAge) {
        logger.debug("Finding user profiles by age range: {} to {}", minAge, maxAge);
        
        var currentDate = LocalDate.now();
        var maxBirthDate = currentDate.minusYears(minAge);
        var minBirthDate = currentDate.minusYears(maxAge + 1);
        
        return userProfileRepository.findByDateOfBirthBetween(minBirthDate, maxBirthDate);
    }
    
    /**
     * Finds recently created user profiles.
     * 
     * @param days number of days to look back
     * @param pageable pagination information
     * @return Page of user profiles
     */
    public Page<UserProfile> findRecentlyCreated(int days, Pageable pageable) {
        logger.debug("Finding user profiles created in last {} days", days);
        var cutoffDate = LocalDateTime.now().minusDays(days);
        return userProfileRepository.findByCreatedDateAfter(cutoffDate, pageable);
    }
    
    /**
     * Finds user profiles with website.
     * 
     * @param pageable pagination information
     * @return Page of user profiles
     */
    public Page<UserProfile> findWithWebsite(Pageable pageable) {
        logger.debug("Finding user profiles with website");
        return userProfileRepository.findByWebsiteIsNotNull(pageable);
    }
    
    /**
     * Finds user profiles with phone number.
     * 
     * @param pageable pagination information
     * @return Page of user profiles
     */
    public Page<UserProfile> findWithPhoneNumber(Pageable pageable) {
        logger.debug("Finding user profiles with phone number");
        return userProfileRepository.findByPhoneNumberIsNotNull(pageable);
    }
    
    /**
     * Finds public user profiles.
     * 
     * @param pageable pagination information
     * @return Page of public user profiles
     */
    public Page<UserProfile> findPublicProfiles(Pageable pageable) {
        logger.debug("Finding public user profiles");
        return userProfileRepository.findByPrivacySetting(true, pageable);
    }
    
    /**
     * Finds private user profiles.
     * 
     * @param pageable pagination information
     * @return Page of private user profiles
     */
    public Page<UserProfile> findPrivateProfiles(Pageable pageable) {
        logger.debug("Finding private user profiles");
        return userProfileRepository.findByPrivacySetting(false, pageable);
    }
    
    /**
     * Finds user profiles by preference.
     * 
     * @param key the preference key
     * @param value the preference value
     * @return List of user profiles
     */
    public List<UserProfile> findByPreference(String key, String value) {
        logger.debug("Finding user profiles by preference: {} = {}", key, value);
        return userProfileRepository.findByPreference(key, value);
    }
    
    /**
     * Gets count of user profiles by location.
     * 
     * @param location the location to count
     * @return number of profiles in the location
     */
    public long countByLocation(String location) {
        logger.debug("Counting user profiles by location: {}", location);
        return userProfileRepository.countByLocation(location);
    }
    
    /**
     * Finds user profiles with complete information (bio and location).
     * 
     * @param pageable pagination information
     * @return Page of complete user profiles
     */
    public Page<UserProfile> findCompleteProfiles(Pageable pageable) {
        logger.debug("Finding user profiles with complete information");
        return userProfileRepository.findProfilesWithBioAndLocation(pageable);
    }
    
    /**
     * Finds recently updated user profiles.
     * 
     * @param days number of days to look back
     * @param pageable pagination information
     * @return Page of recently updated user profiles
     */
    public Page<UserProfile> findRecentlyUpdated(int days, Pageable pageable) {
        logger.debug("Finding user profiles updated in last {} days", days);
        var cutoffDate = LocalDateTime.now().minusDays(days);
        return userProfileRepository.findByLastModifiedDateAfter(cutoffDate, pageable);
    }
    
    /**
     * Deletes a user profile by ID.
     * 
     * @param id the user profile ID
     * @throws IllegalArgumentException if profile doesn't exist     */
    @CacheEvict(value = "userProfiles", key = "#id")
    public void deleteById(String id) {
        logger.debug("Deleting user profile by ID: {}", id);
        
        if (!userProfileRepository.existsById(id)) {
            throw new IllegalArgumentException("User profile with ID " + id + " not found");
        }
        
        userProfileRepository.deleteById(id);
        logger.info("Deleted user profile with ID: {}", id);
    }
    
    /**
     * Deletes a user profile by user ID.
     * 
     * @param userId the user ID
     * @return number of deleted profiles
     */
    public long deleteByUserId(Long userId) {
        logger.debug("Deleting user profile by user ID: {}", userId);
        var deletedCount = userProfileRepository.deleteByUserId(userId);
        logger.info("Deleted {} user profile(s) for user ID: {}", deletedCount, userId);
        return deletedCount;
    }
    
    /**
     * Checks if a user profile exists for a user ID.
     * 
     * @param userId the user ID
     * @return true if profile exists, false otherwise
     */
    public boolean existsByUserId(Long userId) {
        logger.debug("Checking if user profile exists for user ID: {}", userId);
        return userProfileRepository.existsByUserId(userId);
    }
    
    /**
     * Gets total count of user profiles.
     * 
     * @return total number of user profiles
     */
    public long count() {
        logger.debug("Getting total count of user profiles");
        return userProfileRepository.count();
    }
}
