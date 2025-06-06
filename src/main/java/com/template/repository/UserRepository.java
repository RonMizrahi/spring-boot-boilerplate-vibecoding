package com.template.repository;

import com.template.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * Extends JpaRepository for standard CRUD operations with custom query methods.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Finds a user by username.
     * 
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Finds a user by email address.
     * 
     * @param email the email address to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Finds a user by username or email address.
     * 
     * @param username the username to search for
     * @param email the email address to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    /**
     * Checks if a user exists by username.
     * 
     * @param username the username to check
     * @return true if user exists, false otherwise
     */
    boolean existsByUsername(String username);
    
    /**
     * Checks if a user exists by email address.
     * 
     * @param email the email address to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Finds all enabled users.
     * 
     * @return list of enabled users
     */
    List<User> findByEnabledTrue();
    
    /**
     * Finds all disabled users.
     * 
     * @return list of disabled users
     */
    List<User> findByEnabledFalse();
    
    /**
     * Finds users created after a specific date.
     * 
     * @param date the date to search from
     * @return list of users created after the date
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Finds users by username containing the given string (case-insensitive).
     * 
     * @param username the username pattern to search for
     * @param pageable pagination information
     * @return page of matching users
     */
    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
    
    /**
     * Finds users by email containing the given string (case-insensitive).
     * 
     * @param email the email pattern to search for
     * @param pageable pagination information
     * @return page of matching users
     */
    Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    
    /**
     * Custom query to find users by first name or last name.
     * Uses JPQL for demonstration of custom queries.
     * 
     * @param name the name to search for
     * @return list of matching users
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByNameContaining(@Param("name") String name);
    
    /**
     * Custom query to find active users (enabled, not expired, not locked).
     * 
     * @return list of active users
     */
    @Query("SELECT u FROM User u WHERE " +
           "u.enabled = true AND " +
           "u.accountNonExpired = true AND " +
           "u.accountNonLocked = true AND " +
           "u.credentialsNonExpired = true")
    List<User> findActiveUsers();
    
    /**
     * Custom query to count users created within a date range.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return count of users created in the range
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    Long countUsersCreatedBetween(@Param("startDate") LocalDateTime startDate, 
                                  @Param("endDate") LocalDateTime endDate);
    
    /**
     * Custom native query to find users with specific email domains.
     * Demonstrates native SQL usage.
     * 
     * @param domain the email domain to search for
     * @return list of users with the specified domain
     */
    @Query(value = "SELECT * FROM users WHERE email LIKE CONCAT('%@', :domain)", 
           nativeQuery = true)
    List<User> findByEmailDomain(@Param("domain") String domain);
}
