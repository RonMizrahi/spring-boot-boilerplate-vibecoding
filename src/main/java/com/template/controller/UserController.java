package com.template.controller;

import com.template.dto.CreateUserRequestDTO;
import com.template.dto.UserDTO;
import com.template.entity.Role;
import com.template.entity.User;
import com.template.service.UserService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST Controller for User entity operations.
 * Provides comprehensive CRUD operations with proper HTTP status codes.
 * Uses DTOs to separate internal entities from external API.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Create a new user.
     * 
     * @param createUserRequest the user creation request
     * @return created user DTO
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserRequestDTO createUserRequest) {
        logger.debug("Creating new user with username: {}", createUserRequest.getUsername());
        
        // Check if username or email already exists
        if (userService.existsByUsername(createUserRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if (userService.existsByEmail(createUserRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        // Create user entity from DTO
        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        user.setEmail(createUserRequest.getEmail());
        user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        user.setFirstName(createUserRequest.getFirstName());
        user.setLastName(createUserRequest.getLastName());
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        
        User createdUser = userService.createUser(user);
        UserDTO userDTO = convertToDTO(createdUser);
        
        logger.info("Successfully created user with ID: {}", createdUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }
    
    /**
     * Get user by ID.
     * 
     * @param id the user ID
     * @return user DTO
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        logger.debug("Fetching user with ID: {}", id);
        
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            UserDTO userDTO = convertToDTO(user.get());
            return ResponseEntity.ok(userDTO);
        } else {
            logger.debug("User not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get user by username.
     * 
     * @param username the username
     * @return user DTO
     */
    @GetMapping("/username/{username}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        logger.debug("Fetching user with username: {}", username);
        
        Optional<User> user = userService.findByUsername(username);
        if (user.isPresent()) {
            UserDTO userDTO = convertToDTO(user.get());
            return ResponseEntity.ok(userDTO);
        } else {
            logger.debug("User not found with username: {}", username);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get user by email.
     * 
     * @param email the email
     * @return user DTO
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        logger.debug("Fetching user with email: {}", email);
        
        Optional<User> user = userService.findByEmail(email);
        if (user.isPresent()) {
            UserDTO userDTO = convertToDTO(user.get());
            return ResponseEntity.ok(userDTO);
        } else {
            logger.debug("User not found with email: {}", email);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get all users with pagination.
     * 
     * @param page page number (0-based)
     * @param size page size
     * @param sort sort field and direction
     * @return page of user DTOs
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {
        
        logger.debug("Fetching all users - page: {}, size: {}, sort: {}", page, size, sort);
        
        // Parse sort parameter
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1]) 
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<User> users = userService.findAll(pageable);
        Page<UserDTO> userDTOs = users.map(this::convertToDTO);
        
        return ResponseEntity.ok(userDTOs);
    }
    
    /**
     * Get active users.
     * 
     * @return list of active user DTOs
     */
    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserDTO>> getActiveUsers() {
        logger.debug("Fetching active users");
        
        List<User> activeUsers = userService.findActiveUsers();
        List<UserDTO> userDTOs = activeUsers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(userDTOs);
    }
    
    /**
     * Search users by username.
     * 
     * @param username the username pattern
     * @param page page number
     * @param size page size
     * @return page of matching user DTOs
     */
    @GetMapping("/search/username")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<UserDTO>> searchByUsername(
            @RequestParam String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        logger.debug("Searching users by username: {}", username);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userService.searchByUsername(username, pageable);
        Page<UserDTO> userDTOs = users.map(this::convertToDTO);
        
        return ResponseEntity.ok(userDTOs);
    }
    
    /**
     * Search users by name (first name or last name).
     * 
     * @param name the name pattern
     * @return list of matching user DTOs
     */
    @GetMapping("/search/name")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserDTO>> searchByName(@RequestParam String name) {
        logger.debug("Searching users by name: {}", name);
        
        List<User> users = userService.searchByName(name);
        List<UserDTO> userDTOs = users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(userDTOs);
    }
    
    /**
     * Update an existing user.
     * 
     * @param id the user ID
     * @param userDTO the updated user data
     * @return updated user DTO
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        logger.debug("Updating user with ID: {}", id);
        
        Optional<User> existingUserOpt = userService.findById(id);
        if (existingUserOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        User existingUser = existingUserOpt.get();
        
        // Check for username/email conflicts (if changed)
        if (!existingUser.getUsername().equals(userDTO.getUsername()) && 
            userService.existsByUsername(userDTO.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if (!existingUser.getEmail().equals(userDTO.getEmail()) && 
            userService.existsByEmail(userDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        // Update user fields
        existingUser.setUsername(userDTO.getUsername());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        
        // Only admin can update account status fields
        if (userDTO.getEnabled() != null) {
            existingUser.setEnabled(userDTO.getEnabled());
        }
        if (userDTO.getAccountNonExpired() != null) {
            existingUser.setAccountNonExpired(userDTO.getAccountNonExpired());
        }
        if (userDTO.getAccountNonLocked() != null) {
            existingUser.setAccountNonLocked(userDTO.getAccountNonLocked());
        }
        if (userDTO.getCredentialsNonExpired() != null) {
            existingUser.setCredentialsNonExpired(userDTO.getCredentialsNonExpired());
        }
        
        User updatedUser = userService.updateUser(existingUser);
        UserDTO updatedDTO = convertToDTO(updatedUser);
        
        logger.info("Successfully updated user with ID: {}", id);
        return ResponseEntity.ok(updatedDTO);
    }
    
    /**
     * Enable a user.
     * 
     * @param id the user ID
     * @return no content
     */
    @PatchMapping("/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> enableUser(@PathVariable Long id) {
        logger.debug("Enabling user with ID: {}", id);
        
        try {
            userService.enableUser(id);
            logger.info("Successfully enabled user with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.error("Error enabling user with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Disable a user.
     * 
     * @param id the user ID
     * @return no content
     */
    @PatchMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> disableUser(@PathVariable Long id) {
        logger.debug("Disabling user with ID: {}", id);
        
        try {
            userService.disableUser(id);
            logger.info("Successfully disabled user with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.error("Error disabling user with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Delete a user.
     * 
     * @param id the user ID
     * @return no content
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.debug("Deleting user with ID: {}", id);
        
        try {
            userService.deleteUser(id);
            logger.info("Successfully deleted user with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.error("Error deleting user with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get user count statistics.
     * 
     * @param startDate start date for filtering (optional)
     * @param endDate end date for filtering (optional)
     * @return user count
     */
    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getUserCount(
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        
        logger.debug("Getting user count between {} and {}", startDate, endDate);
        
        if (startDate != null && endDate != null) {
            Long count = userService.countUsersCreatedBetween(startDate, endDate);
            return ResponseEntity.ok(count);
        } else {
            // If no dates provided, return total count from pagination
            Page<User> users = userService.findAll(PageRequest.of(0, 1));
            return ResponseEntity.ok(users.getTotalElements());
        }
    }
    
    /**
     * Check if username exists.
     * 
     * @param username the username to check
     * @return boolean response
     */
    @GetMapping("/exists/username/{username}")
    public ResponseEntity<Boolean> checkUsernameExists(@PathVariable String username) {
        logger.debug("Checking if username exists: {}", username);
        
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(exists);
    }
    
    /**
     * Check if email exists.
     * 
     * @param email the email to check
     * @return boolean response
     */
    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        logger.debug("Checking if email exists: {}", email);
        
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }
    
    /**
     * Converts User entity to UserDTO.
     * 
     * @param user the user entity
     * @return user DTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEnabled(user.getEnabled());
        dto.setAccountNonExpired(user.getAccountNonExpired());
        dto.setAccountNonLocked(user.getAccountNonLocked());
        dto.setCredentialsNonExpired(user.getCredentialsNonExpired());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        
        // Convert roles to string set
        if (user.getRoles() != null) {
            Set<String> roleNames = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());
            dto.setRoles(roleNames);
        }
        
        return dto;
    }
}
