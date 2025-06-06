package com.template.service;

import com.template.entity.User;
import com.template.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * Custom UserDetailsService implementation for Spring Security.
 * Loads user-specific data from the database for authentication.
 * Uses Java 21 features and Spring best practices.
 */
@Service
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UserRepository userRepository;

    /**
     * Constructor injection for UserRepository.
     * 
     * @param userRepository the user repository
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads user by username for Spring Security authentication.
     * Supports both username and email as login identifiers.
     * 
     * @param username the username or email
     * @return UserDetails for authentication
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Loading user by username: {}", username);

        // Try to find user by username first, then by email
        User user = userRepository.findByUsername(username)
            .or(() -> userRepository.findByEmail(username))
            .orElseThrow(() -> {
                logger.warn("User not found with username or email: {}", username);
                return new UsernameNotFoundException("User not found with username or email: " + username);
            });

        logger.debug("Found user: {} (ID: {})", user.getUsername(), user.getId());

        return buildUserDetails(user);
    }

    /**
     * Builds Spring Security UserDetails from our User entity.
     * 
     * @param user the user entity
     * @return UserDetails implementation
     */
    private UserDetails buildUserDetails(User user) {
        // For now, we'll assign a default role. In Unit 10, we'll implement proper role-based authorization
        List<GrantedAuthority> authorities = getAuthorities(user);

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .authorities(authorities)
            .accountExpired(!user.getAccountNonExpired())
            .accountLocked(!user.getAccountNonLocked())
            .credentialsExpired(!user.getCredentialsNonExpired())
            .disabled(!user.getEnabled())
            .build();
    }

    /**
     * Gets authorities (roles/permissions) for the user.
     * Currently assigns a default role - will be enhanced in Unit 10 with proper RBAC.
     * 
     * @param user the user entity
     * @return list of granted authorities
     */
    private List<GrantedAuthority> getAuthorities(User user) {
        // For now, assign a default role to all users
        // In Unit 10, we'll implement proper role-based authorization
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /**
     * Loads user by ID - useful for token-based authentication.
     * Will be used in Unit 9 for JWT authentication.
     * 
     * @param userId the user ID
     * @return UserDetails for authentication
     * @throws UsernameNotFoundException if user not found
     */
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        logger.debug("Loading user by ID: {}", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                logger.warn("User not found with ID: {}", userId);
                return new UsernameNotFoundException("User not found with ID: " + userId);
            });

        logger.debug("Found user: {} (ID: {})", user.getUsername(), user.getId());

        return buildUserDetails(user);
    }
}
