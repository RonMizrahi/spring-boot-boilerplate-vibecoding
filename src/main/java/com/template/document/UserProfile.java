package com.template.document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * UserProfile document representing user profile information and preferences.
 * Uses MongoDB annotations for document persistence with Java 21 best practices.
 * Complements the User entity by storing profile-specific data.
 */
@Document(collection = "user_profiles")
public class UserProfile {
    
    @Id
    private String id;
    
    @NotNull(message = "User ID is required")
    @Indexed(unique = true)
    @Field("user_id")
    private Long userId;
    
    @Size(max = 200, message = "Bio must not exceed 200 characters")
    @Field("bio")
    private String bio;
    
    @Size(max = 100, message = "Location must not exceed 100 characters")
    @Field("location")
    private String location;
    
    @Size(max = 255, message = "Website URL must not exceed 255 characters")
    @Field("website")
    private String website;
    
    @Size(max = 50, message = "Phone number must not exceed 50 characters")
    @Field("phone_number")
    private String phoneNumber;
    
    @Field("date_of_birth")
    private LocalDateTime dateOfBirth;
    
    @Size(max = 10, message = "Gender must not exceed 10 characters")
    @Field("gender")
    private String gender;
    
    @Size(max = 50, message = "Occupation must not exceed 50 characters")
    @Field("occupation")
    private String occupation;
    
    @Size(max = 100, message = "Company must not exceed 100 characters")
    @Field("company")
    private String company;
    
    @Field("avatar_url")
    private String avatarUrl;
    
    @Field("cover_image_url")
    private String coverImageUrl;
    
    @Field("is_public")
    private Boolean isPublic = true;
    
    @Field("is_verified")
    private Boolean isVerified = false;
    
    @Field("notification_preferences")
    private Map<String, Boolean> notificationPreferences = new HashMap<>();
    
    @Field("privacy_settings")
    private Map<String, String> privacySettings = new HashMap<>();
    
    @Field("user_preferences")
    private Map<String, Object> userPreferences = new HashMap<>();
    
    @Field("social_links")
    private Map<String, String> socialLinks = new HashMap<>();
    
    @Field("tags")
    private java.util.List<String> tags = new java.util.ArrayList<>();
    
    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Default constructor for MongoDB.
     */
    public UserProfile() {
        initializeDefaults();
    }
    
    /**
     * Constructor with required fields.
     * 
     * @param userId the associated user ID
     */
    public UserProfile(Long userId) {
        this.userId = userId;
        initializeDefaults();
    }
    
    /**
     * Initialize default values for maps and preferences.
     */
    private void initializeDefaults() {
        if (notificationPreferences == null) {
            notificationPreferences = new HashMap<>();
            notificationPreferences.put("emailNotifications", true);
            notificationPreferences.put("pushNotifications", true);
            notificationPreferences.put("smsNotifications", false);
            notificationPreferences.put("marketingEmails", false);
        }
        
        if (privacySettings == null) {
            privacySettings = new HashMap<>();
            privacySettings.put("profileVisibility", "public");
            privacySettings.put("emailVisibility", "private");
            privacySettings.put("phoneVisibility", "private");
        }
        
        if (userPreferences == null) {
            userPreferences = new HashMap<>();
            userPreferences.put("language", "en");
            userPreferences.put("timezone", "UTC");
            userPreferences.put("theme", "light");
        }
    }
    
    // Getters and Setters using Java 21 enhanced patterns
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getWebsite() {
        return website;
    }
    
    public void setWebsite(String website) {
        this.website = website;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public LocalDateTime getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getOccupation() {
        return occupation;
    }
    
    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }
    
    public String getCompany() {
        return company;
    }
    
    public void setCompany(String company) {
        this.company = company;
    }
    
    public String getAvatarUrl() {
        return avatarUrl;
    }
    
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    
    public String getCoverImageUrl() {
        return coverImageUrl;
    }
    
    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }
    
    public Boolean getIsPublic() {
        return isPublic;
    }
    
    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
    
    public Boolean getIsVerified() {
        return isVerified;
    }
    
    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }
    
    public Map<String, Boolean> getNotificationPreferences() {
        return notificationPreferences;
    }
    
    public void setNotificationPreferences(Map<String, Boolean> notificationPreferences) {
        this.notificationPreferences = notificationPreferences;
    }
    
    public Map<String, String> getPrivacySettings() {
        return privacySettings;
    }
    
    public void setPrivacySettings(Map<String, String> privacySettings) {
        this.privacySettings = privacySettings;
    }
    
    public Map<String, Object> getUserPreferences() {
        return userPreferences;
    }
    
    public void setUserPreferences(Map<String, Object> userPreferences) {
        this.userPreferences = userPreferences;
    }
    
    public Map<String, String> getSocialLinks() {
        return socialLinks;
    }
    
    public void setSocialLinks(Map<String, String> socialLinks) {
        this.socialLinks = socialLinks;
    }
    
    public java.util.List<String> getTags() {
        return tags;
    }
    
    public void setTags(java.util.List<String> tags) {
        this.tags = tags;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Gets the display name for the profile.
     * Uses company and occupation if available.
     * 
     * @return the display name or null if both are null
     */
    public String getDisplayName() {
        if (company != null && occupation != null) {
            return occupation + " at " + company;
        } else if (occupation != null) {
            return occupation;
        } else if (company != null) {
            return "Employee at " + company;
        }
        return null;
    }
    
    /**
     * Updates a notification preference.
     * 
     * @param key the preference key
     * @param value the preference value
     */
    public void updateNotificationPreference(String key, Boolean value) {
        if (notificationPreferences == null) {
            notificationPreferences = new HashMap<>();
        }
        notificationPreferences.put(key, value);
    }
    
    /**
     * Updates a privacy setting.
     * 
     * @param key the setting key
     * @param value the setting value
     */
    public void updatePrivacySetting(String key, String value) {
        if (privacySettings == null) {
            privacySettings = new HashMap<>();
        }
        privacySettings.put(key, value);
    }
    
    /**
     * Updates a user preference.
     * 
     * @param key the preference key
     * @param value the preference value
     */
    public void updateUserPreference(String key, Object value) {
        if (userPreferences == null) {
            userPreferences = new HashMap<>();
        }
        userPreferences.put(key, value);
    }
    
    /**
     * Adds a social link.
     * 
     * @param platform the social platform (e.g., "twitter", "linkedin")
     * @param url the social profile URL
     */
    public void addSocialLink(String platform, String url) {
        if (socialLinks == null) {
            socialLinks = new HashMap<>();
        }
        socialLinks.put(platform, url);
    }
    
    /**
     * Adds a tag to the profile.
     * 
     * @param tag the tag to add
     */
    public void addTag(String tag) {
        if (tags == null) {
            tags = new java.util.ArrayList<>();
        }
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    /**
     * Removes a tag from the profile.
     * 
     * @param tag the tag to remove
     */
    public void removeTag(String tag) {
        if (tags != null) {
            tags.remove(tag);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile profile = (UserProfile) o;
        return Objects.equals(id, profile.id) && 
               Objects.equals(userId, profile.userId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, userId);
    }
    
    @Override
    public String toString() {
        return String.format("UserProfile{id='%s', userId=%d, bio='%s', location='%s', isPublic=%s, createdAt=%s}", 
                           id, userId, bio, location, isPublic, createdAt);
    }
}
