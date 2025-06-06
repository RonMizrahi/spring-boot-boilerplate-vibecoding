package com.template.service;

import com.template.entity.User;
import com.template.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for User entity operations.
 * Provides business logic layer between controllers and repository.
 * Uses Java 21 features and Spring best practices.
 */
@Service
@Transactional(readOnly = true)
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepository userRepository;
      /**
     * Constructor injection for UserRepository.
     * 
     * @param userRepository the user repository
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Creates a new user.
     * 
     * @param user the user to create     * @return the created user
     * @throws IllegalArgumentException if user data is invalid
     */
    @Transactional
    @CachePut(value = "users", key = "#result.id")
    public User createUser(User user) {
        logger.debug("Creating new user with username: {}", user.getUsername());
          // Validate unique constraints
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username '" + user.getUsername() + "' already exists");
        }
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email '" + user.getEmail() + "' already exists");
        }
        
        var savedUser = userRepository.save(user);
        logger.info("Created user with ID: {} and username: {}", savedUser.getId(), savedUser.getUsername());
        
        return savedUser;
    }
    
    /**
     * Updates an existing user.
     * 
     * @param user the user to update     * @return the updated user
     * @throws IllegalArgumentException if user doesn't exist or data is invalid
     */
    @Transactional
    @CachePut(value = "users", key = "#user.id")
    @CacheEvict(value = "users", key = "#user.username")
    public User updateUser(User user) {
        logger.debug("Updating user with ID: {}", user.getId());
        
        if (user.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null for update");
        }
          if (!userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("User with ID " + user.getId() + " not found");
        }
          // Check unique constraints for other users
        var existingByUsername = userRepository.findByUsername(user.getUsername());
        if (existingByUsername.isPresent() && !existingByUsername.get().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Username '" + user.getUsername() + "' already exists");
        }
        
        var existingByEmail = userRepository.findByEmail(user.getEmail());
        if (existingByEmail.isPresent() && !existingByEmail.get().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Email '" + user.getEmail() + "' already exists");
        }
        
        var updatedUser = userRepository.save(user);
        logger.info("Updated user with ID: {}", updatedUser.getId());
        
        return updatedUser;
    }
    
    /**
     * Finds a user by ID.
     * 
     * @param id the user ID
     * @return Optional containing the user if found     */
    @Cacheable(value = "users", key = "#id")
    public Optional<User> findById(Long id) {
        logger.debug("Finding user by ID: {}", id);
        return userRepository.findById(id);
    }
    
    /**
     * Finds a user by username.
     * 
     * @param username the username
     * @return Optional containing the user if found
     */
    @Cacheable(value = "users", key = "#username")
    public Optional<User> findByUsername(String username) {
        logger.debug("Finding user by username: {}", username);
        return userRepository.findByUsername(username);
    }
    
    /**
     * Finds a user by email.
     * 
     * @param email the email address
     * @return Optional containing the user if found
     */
    public Optional<User> findByEmail(String email) {
        logger.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email);
    }
    
    /**
     * Finds a user by username or email.
     * 
     * @param usernameOrEmail the username or email
     * @return Optional containing the user if found
     */
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        logger.debug("Finding user by username or email: {}", usernameOrEmail);
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
    }
    
    /**
     * Gets all users with pagination.
     * 
     * @param pageable pagination information
     * @return page of users
     */
    public Page<User> findAll(Pageable pageable) {
        logger.debug("Finding all users with pagination: {}", pageable);
        return userRepository.findAll(pageable);
    }
    
    /**
     * Gets all enabled users.
     * 
     * @return list of enabled users
     */
    public List<User> findEnabledUsers() {
        logger.debug("Finding all enabled users");
        return userRepository.findByEnabledTrue();
    }
    
    /**
     * Gets all active users (enabled, not expired, not locked).
     * 
     * @return list of active users
     */
    public List<User> findActiveUsers() {
        logger.debug("Finding all active users");
        return userRepository.findActiveUsers();
    }
    
    /**
     * Searches users by username pattern.
     * 
     * @param username the username pattern
     * @param pageable pagination information
     * @return page of matching users
     */
    public Page<User> searchByUsername(String username, Pageable pageable) {
        logger.debug("Searching users by username pattern: {}", username);
        return userRepository.findByUsernameContainingIgnoreCase(username, pageable);
    }
    
    /**
     * Searches users by email pattern.
     * 
     * @param email the email pattern
     * @param pageable pagination information
     * @return page of matching users
     */
    public Page<User> searchByEmail(String email, Pageable pageable) {
        logger.debug("Searching users by email pattern: {}", email);
        return userRepository.findByEmailContainingIgnoreCase(email, pageable);
    }
    
    /**
     * Searches users by name (first or last name).
     * 
     * @param name the name pattern
     * @return list of matching users
     */
    public List<User> searchByName(String name) {
        logger.debug("Searching users by name pattern: {}", name);
        return userRepository.findByNameContaining(name);
    }
    
    /**
     * Enables a user account.
     * 
     * @param userId the user ID
     * @throws IllegalArgumentException if user not found
     */
    @Transactional
    public void enableUser(Long userId) {        logger.debug("Enabling user with ID: {}", userId);
        
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));
        
        user.setEnabled(true);
        userRepository.save(user);
        
        logger.info("Enabled user with ID: {}", userId);
    }
    
    /**
     * Disables a user account.
     * 
     * @param userId the user ID
     * @throws IllegalArgumentException if user not found
     */
    @Transactional
    public void disableUser(Long userId) {        logger.debug("Disabling user with ID: {}", userId);
        
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));
        
        user.setEnabled(false);
        userRepository.save(user);
        
        logger.info("Disabled user with ID: {}", userId);
    }
    
    /**
     * Deletes a user by ID.     * 
     * @param userId the user ID
     * @throws IllegalArgumentException if user not found
     */
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void deleteUser(Long userId) {        logger.debug("Deleting user with ID: {}", userId);
        
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User with ID " + userId + " not found");
        }
        
        userRepository.deleteById(userId);
        logger.info("Deleted user with ID: {}", userId);
    }
    
    /**
     * Gets count of users created within a date range.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return count of users
     */
    public Long countUsersCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        logger.debug("Counting users created between {} and {}", startDate, endDate);
        return userRepository.countUsersCreatedBetween(startDate, endDate);
    }
    
    /**
     * Checks if a username exists.
     * 
     * @param username the username to check
     * @return true if username exists
     */
    public boolean existsByUsername(String username) {
        logger.debug("Checking if username exists: {}", username);
        return userRepository.existsByUsername(username);
    }
    
    /**
     * Checks if an email exists.
     * 
     * @param email the email to check
     * @return true if email exists
     */
    public boolean existsByEmail(String email) {
        logger.debug("Checking if email exists: {}", email);
        return userRepository.existsByEmail(email);
    }
}
