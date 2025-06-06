package com.template.controller;

import com.template.document.UserProfile;
import com.template.dto.UserProfileDTO;
import com.template.service.UserProfileService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST Controller for UserProfile document operations.
 * Provides comprehensive CRUD operations with proper HTTP status codes.
 * Uses DTOs to separate internal documents from external API.
 */
@RestController
@RequestMapping("/api/user-profiles")
public class UserProfileController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);
    
    private final UserProfileService userProfileService;
    
    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }
    
    /**
     * Create a new user profile.
     * 
     * @param userProfileDTO the user profile data
     * @return created user profile DTO
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDTO> createUserProfile(@Valid @RequestBody UserProfileDTO userProfileDTO) {
        logger.debug("Creating new user profile for user ID: {}", userProfileDTO.getUserId());
        
        // Check if profile already exists for this user
        Optional<UserProfile> existingProfile = userProfileService.findByUserId(userProfileDTO.getUserId());
        if (existingProfile.isPresent()) {
            logger.debug("User profile already exists for user ID: {}", userProfileDTO.getUserId());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        // Convert DTO to entity
        UserProfile userProfile = convertToEntity(userProfileDTO);
        userProfile.setCreatedAt(LocalDateTime.now());
        userProfile.setUpdatedAt(LocalDateTime.now());
        
        UserProfile createdProfile = userProfileService.createUserProfile(userProfile);
        UserProfileDTO responseDTO = convertToDTO(createdProfile);
        
        logger.info("Successfully created user profile with ID: {}", createdProfile.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
    
    /**
     * Get user profile by ID.
     * 
     * @param id the profile ID
     * @return user profile DTO
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDTO> getUserProfileById(@PathVariable String id) {
        logger.debug("Fetching user profile with ID: {}", id);
        
        Optional<UserProfile> profile = userProfileService.findById(id);
        if (profile.isPresent()) {
            UserProfileDTO dto = convertToDTO(profile.get());
            return ResponseEntity.ok(dto);
        } else {
            logger.debug("User profile not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get user profile by user ID.
     * 
     * @param userId the user ID
     * @return user profile DTO
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDTO> getUserProfileByUserId(@PathVariable Long userId) {
        logger.debug("Fetching user profile for user ID: {}", userId);
        
        Optional<UserProfile> profile = userProfileService.findByUserId(userId);
        if (profile.isPresent()) {
            UserProfileDTO dto = convertToDTO(profile.get());
            return ResponseEntity.ok(dto);
        } else {
            logger.debug("User profile not found for user ID: {}", userId);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get all user profiles with pagination.
     * 
     * @param page page number (0-based)
     * @param size page size
     * @param sort sort field and direction
     * @return page of user profile DTOs
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserProfileDTO>> getAllUserProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        
        logger.debug("Fetching all user profiles - page: {}, size: {}, sort: {}", page, size, sort);
        
        // Parse sort parameter
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1]) 
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<UserProfile> profiles = userProfileService.findAll(pageable);
        Page<UserProfileDTO> profileDTOs = profiles.map(this::convertToDTO);
        
        return ResponseEntity.ok(profileDTOs);
    }
    
    /**
     * Get public user profiles.
     * 
     * @param page page number
     * @param size page size
     * @return page of public user profile DTOs
     */
    @GetMapping("/public")
    public ResponseEntity<Page<UserProfileDTO>> getPublicUserProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        logger.debug("Fetching public user profiles - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserProfile> profiles = userProfileService.findPublicProfiles(pageable);
        Page<UserProfileDTO> profileDTOs = profiles.map(this::convertToDTO);
        
        return ResponseEntity.ok(profileDTOs);
    }
    
    /**
     * Search user profiles by location.
     * 
     * @param location the location
     * @param page page number
     * @param size page size
     * @return page of matching user profile DTOs
     */
    @GetMapping("/search/location")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<UserProfileDTO>> searchByLocation(
            @RequestParam String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        logger.debug("Searching user profiles by location: {}", location);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<UserProfile> profiles = userProfileService.findByLocation(location, pageable);
        Page<UserProfileDTO> profileDTOs = profiles.map(this::convertToDTO);
        
        return ResponseEntity.ok(profileDTOs);
    }
    
    /**
     * Search user profiles by bio keywords.
     * 
     * @param keywords the keywords to search in bio
     * @param page page number
     * @param size page size
     * @return page of matching user profile DTOs
     */
    @GetMapping("/search/bio")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<UserProfileDTO>> searchByBio(
            @RequestParam String keywords,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        logger.debug("Searching user profiles by bio keywords: {}", keywords);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<UserProfile> profiles = userProfileService.searchByBio(keywords, pageable);
        Page<UserProfileDTO> profileDTOs = profiles.map(this::convertToDTO);
        
        return ResponseEntity.ok(profileDTOs);
    }
    
    /**
     * Get user profiles by age range.
     * 
     * @param minAge minimum age
     * @param maxAge maximum age
     * @return list of user profile DTOs in age range
     */
    @GetMapping("/search/age-range")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserProfileDTO>> getUserProfilesByAgeRange(
            @RequestParam int minAge,
            @RequestParam int maxAge) {
        
        logger.debug("Searching user profiles by age range: {} to {}", minAge, maxAge);
        
        List<UserProfile> profiles = userProfileService.findByAgeRange(minAge, maxAge);
        List<UserProfileDTO> profileDTOs = profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(profileDTOs);
    }
    
    /**
     * Get recently created user profiles.
     * 
     * @param days number of days to look back
     * @param page page number
     * @param size page size
     * @return page of recently created user profile DTOs
     */
    @GetMapping("/recent")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<UserProfileDTO>> getRecentlyCreatedProfiles(
            @RequestParam(defaultValue = "7") int days,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        logger.debug("Fetching recently created user profiles - days: {}, page: {}, size: {}", days, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<UserProfile> profiles = userProfileService.findRecentlyCreated(days, pageable);
        Page<UserProfileDTO> profileDTOs = profiles.map(this::convertToDTO);
        
        return ResponseEntity.ok(profileDTOs);
    }
    
    /**
     * Get user profiles with website.
     * 
     * @param page page number
     * @param size page size
     * @return page of user profile DTOs with website
     */
    @GetMapping("/with-website")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<UserProfileDTO>> getProfilesWithWebsite(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        logger.debug("Fetching user profiles with website");
        
        Pageable pageable = PageRequest.of(page, size);
        Page<UserProfile> profiles = userProfileService.findWithWebsite(pageable);
        Page<UserProfileDTO> profileDTOs = profiles.map(this::convertToDTO);
        
        return ResponseEntity.ok(profileDTOs);
    }
    
    /**
     * Update an existing user profile.
     * 
     * @param id the profile ID
     * @param userProfileDTO the updated profile data
     * @return updated user profile DTO
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() and (#userProfileDTO.userId == authentication.principal.id or hasRole('ADMIN'))")
    public ResponseEntity<UserProfileDTO> updateUserProfile(@PathVariable String id, @Valid @RequestBody UserProfileDTO userProfileDTO) {
        logger.debug("Updating user profile with ID: {}", id);
        
        Optional<UserProfile> existingProfileOpt = userProfileService.findById(id);
        if (existingProfileOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        UserProfile existingProfile = existingProfileOpt.get();
        
        // Update profile fields
        existingProfile.setBio(userProfileDTO.getBio());
        existingProfile.setLocation(userProfileDTO.getLocation());
        existingProfile.setWebsite(userProfileDTO.getWebsite());
        existingProfile.setPhoneNumber(userProfileDTO.getPhoneNumber());
        existingProfile.setDateOfBirth(userProfileDTO.getDateOfBirth());
        existingProfile.setGender(userProfileDTO.getGender());
        existingProfile.setOccupation(userProfileDTO.getOccupation());
        existingProfile.setCompany(userProfileDTO.getCompany());
        existingProfile.setAvatarUrl(userProfileDTO.getAvatarUrl());
        existingProfile.setCoverImageUrl(userProfileDTO.getCoverImageUrl());
        existingProfile.setIsPublic(userProfileDTO.getIsPublic());
        existingProfile.setNotificationPreferences(userProfileDTO.getNotificationPreferences());
        existingProfile.setPrivacySettings(userProfileDTO.getPrivacySettings());
        existingProfile.setUserPreferences(userProfileDTO.getUserPreferences());
        existingProfile.setUpdatedAt(LocalDateTime.now());
        
        UserProfile updatedProfile = userProfileService.updateUserProfile(existingProfile);
        UserProfileDTO updatedDTO = convertToDTO(updatedProfile);
        
        logger.info("Successfully updated user profile with ID: {}", id);
        return ResponseEntity.ok(updatedDTO);
    }
    
    /**
     * Delete a user profile.
     * 
     * @param id the profile ID
     * @return no content
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserProfile(@PathVariable String id) {
        logger.debug("Deleting user profile with ID: {}", id);
        
        try {
            userProfileService.deleteById(id);
            logger.info("Successfully deleted user profile with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.error("Error deleting user profile with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get user profile count by location.
     * 
     * @param location the location
     * @return count of profiles in location
     */
    @GetMapping("/count/location/{location}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> getProfileCountByLocation(@PathVariable String location) {
        logger.debug("Getting user profile count for location: {}", location);
        
        long count = userProfileService.countByLocation(location);
        return ResponseEntity.ok(count);
    }
    
    /**
     * Search user profiles by preference.
     * 
     * @param key preference key
     * @param value preference value
     * @return list of matching user profile DTOs
     */
    @GetMapping("/search/preference")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserProfileDTO>> searchByPreference(
            @RequestParam String key,
            @RequestParam String value) {
        
        logger.debug("Searching user profiles by preference: {} = {}", key, value);
        
        List<UserProfile> profiles = userProfileService.findByPreference(key, value);
        List<UserProfileDTO> profileDTOs = profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(profileDTOs);
    }
    
    /**
     * Converts UserProfile document to UserProfileDTO.
     * 
     * @param profile the user profile document
     * @return user profile DTO
     */
    private UserProfileDTO convertToDTO(UserProfile profile) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(profile.getId());
        dto.setUserId(profile.getUserId());
        dto.setBio(profile.getBio());
        dto.setLocation(profile.getLocation());
        dto.setWebsite(profile.getWebsite());
        dto.setPhoneNumber(profile.getPhoneNumber());
        dto.setDateOfBirth(profile.getDateOfBirth());
        dto.setGender(profile.getGender());
        dto.setOccupation(profile.getOccupation());
        dto.setCompany(profile.getCompany());
        dto.setAvatarUrl(profile.getAvatarUrl());
        dto.setCoverImageUrl(profile.getCoverImageUrl());
        dto.setIsPublic(profile.getIsPublic());
        dto.setIsVerified(profile.getIsVerified());
        dto.setNotificationPreferences(profile.getNotificationPreferences());
        dto.setPrivacySettings(profile.getPrivacySettings());
        dto.setUserPreferences(profile.getUserPreferences());
        dto.setCreatedAt(profile.getCreatedAt());
        dto.setUpdatedAt(profile.getUpdatedAt());
        
        return dto;
    }
    
    /**
     * Converts UserProfileDTO to UserProfile document.
     * 
     * @param dto the user profile DTO
     * @return user profile document
     */
    private UserProfile convertToEntity(UserProfileDTO dto) {
        UserProfile profile = new UserProfile();
        profile.setUserId(dto.getUserId());
        profile.setBio(dto.getBio());
        profile.setLocation(dto.getLocation());
        profile.setWebsite(dto.getWebsite());
        profile.setPhoneNumber(dto.getPhoneNumber());
        profile.setDateOfBirth(dto.getDateOfBirth());
        profile.setGender(dto.getGender());
        profile.setOccupation(dto.getOccupation());
        profile.setCompany(dto.getCompany());
        profile.setAvatarUrl(dto.getAvatarUrl());
        profile.setCoverImageUrl(dto.getCoverImageUrl());
        profile.setIsPublic(dto.getIsPublic() != null ? dto.getIsPublic() : true);
        profile.setIsVerified(dto.getIsVerified() != null ? dto.getIsVerified() : false);
        profile.setNotificationPreferences(dto.getNotificationPreferences());
        profile.setPrivacySettings(dto.getPrivacySettings());
        profile.setUserPreferences(dto.getUserPreferences());
        
        return profile;
    }
}
