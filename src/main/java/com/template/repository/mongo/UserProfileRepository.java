package com.template.repository.mongo;

import com.template.document.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for UserProfile document operations.
 * Extends MongoRepository for standard CRUD operations with custom query methods.
 */
@Repository
public interface UserProfileRepository extends MongoRepository<UserProfile, String> {
    
    /**
     * Finds a user profile by user ID.
     * 
     * @param userId the user ID to search for
     * @return Optional containing the user profile if found
     */
    Optional<UserProfile> findByUserId(Long userId);
    
    /**
     * Checks if a user profile exists by user ID.
     * 
     * @param userId the user ID to check
     * @return true if profile exists, false otherwise
     */
    boolean existsByUserId(Long userId);
    
    /**
     * Finds user profiles by location (case-insensitive).
     * 
     * @param location the location to search for
     * @param pageable pagination information
     * @return Page of user profiles
     */
    Page<UserProfile> findByLocationIgnoreCase(String location, Pageable pageable);
    
    /**
     * Finds user profiles containing bio keywords (case-insensitive).
     * 
     * @param keywords the keywords to search in bio
     * @param pageable pagination information
     * @return Page of user profiles
     */
    Page<UserProfile> findByBioContainingIgnoreCase(String keywords, Pageable pageable);
    
    /**
     * Finds user profiles by date of birth range.
     * 
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return List of user profiles
     */
    List<UserProfile> findByDateOfBirthBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Finds user profiles created after a specific date.
     * 
     * @param date the date to compare
     * @param pageable pagination information
     * @return Page of user profiles
     */
    Page<UserProfile> findByCreatedDateAfter(LocalDateTime date, Pageable pageable);
    
    /**
     * Finds user profiles that have a website.
     * 
     * @param pageable pagination information
     * @return Page of user profiles
     */
    Page<UserProfile> findByWebsiteIsNotNull(Pageable pageable);
    
    /**
     * Finds user profiles that have a phone number.
     * 
     * @param pageable pagination information
     * @return Page of user profiles
     */
    Page<UserProfile> findByPhoneNumberIsNotNull(Pageable pageable);
    
    /**
     * Finds user profiles by privacy setting using custom query.
     * 
     * @param isPublic true for public profiles, false for private
     * @param pageable pagination information
     * @return Page of user profiles
     */
    @Query("{ 'isPublic' : ?0 }")
    Page<UserProfile> findByPrivacySetting(boolean isPublic, Pageable pageable);
    
    /**
     * Finds user profiles with specific preferences using custom query.
     * 
     * @param key the preference key
     * @param value the preference value
     * @return List of user profiles
     */
    @Query("{ 'preferences.?0' : ?1 }")
    List<UserProfile> findByPreference(String key, String value);
    
    /**
     * Counts user profiles by location.
     * 
     * @param location the location to count
     * @return number of profiles in the location
     */
    long countByLocation(String location);
    
    /**
     * Deletes user profile by user ID.
     * 
     * @param userId the user ID
     * @return number of deleted documents
     */
    long deleteByUserId(Long userId);
    
    /**
     * Finds user profiles with bio and location using custom aggregation.
     * 
     * @param pageable pagination information
     * @return Page of user profiles that have both bio and location
     */
    @Query("{ $and: [ { 'bio': { $ne: null } }, { 'location': { $ne: null } } ] }")
    Page<UserProfile> findProfilesWithBioAndLocation(Pageable pageable);
    
    /**
     * Finds recently updated profiles (within last 30 days).
     * 
     * @param thirtyDaysAgo the date 30 days ago
     * @param pageable pagination information
     * @return Page of recently updated user profiles
     */
    Page<UserProfile> findByLastModifiedDateAfter(LocalDateTime thirtyDaysAgo, Pageable pageable);
}
