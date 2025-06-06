package com.template.security;

import com.template.entity.User;
import com.template.repository.UserRepository;
import com.template.repository.RoleRepository;
import com.template.repository.PermissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for handling authorization logic and permission checks.
 * Provides methods to check user permissions and roles programmatically.
 */
@Service
public class AuthorizationService {
      private static final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    
    /**
     * Constructor injection for repositories.
     * 
     * @param userRepository the user repository
     * @param roleRepository the role repository
     * @param permissionRepository the permission repository
     */
    public AuthorizationService(UserRepository userRepository, 
                               RoleRepository roleRepository,
                               PermissionRepository permissionRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }
      /**
     * Get the currently authenticated user.
     * 
     * @return Optional containing the current user, or empty if not authenticated
     */
    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }
        
        String username;
        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            username = (String) authentication.getPrincipal();
        } else {
            return Optional.empty();
        }
        
        return userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username));
    }
    
    /**
     * Check if the current user has a specific role.
     * 
     * @param roleName the role name to check (without ROLE_ prefix)
     * @return true if the user has the role
     */
    public boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_" + roleName));
    }
    
    /**
     * Check if the current user has a specific permission.
     * 
     * @param permissionName the permission name to check
     * @return true if the user has the permission
     */
    public boolean hasPermission(String permissionName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(permissionName));
    }
    
    /**
     * Check if the current user has permission for a resource and action.
     * 
     * @param resource the resource type
     * @param action the action type
     * @return true if the user has permission
     */
    public boolean hasPermission(String resource, String action) {
        String permissionName = resource + ":" + action;
        return hasPermission(permissionName);
    }
    
    /**
     * Check if the current user is an admin.
     * 
     * @return true if the user has ADMIN role
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }
    
    /**
     * Check if the current user can access a specific user's data.
     * Users can access their own data, admins can access any user's data.
     * 
     * @param targetUserId the ID of the user whose data is being accessed
     * @return true if access is allowed
     */
    public boolean canAccessUser(Long targetUserId) {
        if (isAdmin()) {
            return true;
        }
        
        Optional<User> currentUser = getCurrentUser();
        return currentUser.map(user -> user.getId().equals(targetUserId)).orElse(false);
    }
    
    /**
     * Check if the current user can modify a specific user's data.
     * Users can modify their own data, admins can modify any user's data.
     * 
     * @param targetUserId the ID of the user whose data is being modified
     * @return true if modification is allowed
     */
    public boolean canModifyUser(Long targetUserId) {
        return canAccessUser(targetUserId); // Same logic for now
    }
    
    /**
     * Check if the current user has any of the specified roles.
     * 
     * @param roleNames the role names to check
     * @return true if the user has any of the roles
     */
    public boolean hasAnyRole(String... roleNames) {
        for (String roleName : roleNames) {
            if (hasRole(roleName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if the current user has all of the specified roles.
     * 
     * @param roleNames the role names to check
     * @return true if the user has all the roles
     */
    public boolean hasAllRoles(String... roleNames) {
        for (String roleName : roleNames) {
            if (!hasRole(roleName)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Check if the current user has any of the specified permissions.
     * 
     * @param permissionNames the permission names to check
     * @return true if the user has any of the permissions
     */
    public boolean hasAnyPermission(String... permissionNames) {
        for (String permissionName : permissionNames) {
            if (hasPermission(permissionName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get the current user's username.
     * 
     * @return Optional containing the username, or empty if not authenticated
     */
    public Optional<String> getCurrentUsername() {
        return getCurrentUser().map(User::getUsername);
    }
    
    /**
     * Check if a role exists by name.
     * 
     * @param roleName the role name
     * @return true if the role exists
     */
    public boolean roleExists(String roleName) {
        return roleRepository.existsByName(roleName);
    }
    
    /**
     * Check if a permission exists by name.
     * 
     * @param permissionName the permission name
     * @return true if the permission exists
     */
    public boolean permissionExists(String permissionName) {
        return permissionRepository.existsByName(permissionName);
    }
    
    /**
     * Log security events for auditing purposes.
     * 
     * @param action the security action performed
     * @param details additional details about the action
     */
    public void logSecurityEvent(String action, String details) {
        Optional<String> username = getCurrentUsername();
        logger.info("Security Event - Action: {}, User: {}, Details: {}", 
                   action, username.orElse("anonymous"), details);
    }
}
