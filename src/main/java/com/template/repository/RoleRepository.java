package com.template.repository;

import com.template.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * Repository interface for Role entity operations.
 * Provides database access methods for role management.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * Find role by name.
     * 
     * @param name the role name
     * @return Optional containing the role if found
     */
    Optional<Role> findByName(String name);
    
    /**
     * Find role by name ignoring case.
     * 
     * @param name the role name
     * @return Optional containing the role if found
     */
    Optional<Role> findByNameIgnoreCase(String name);
    
    /**
     * Check if role exists by name.
     * 
     * @param name the role name
     * @return true if role exists
     */
    boolean existsByName(String name);
    
    /**
     * Check if role exists by name ignoring case.
     * 
     * @param name the role name
     * @return true if role exists
     */
    boolean existsByNameIgnoreCase(String name);
    
    /**
     * Find all enabled roles.
     * 
     * @return Set of enabled roles
     */
    Set<Role> findAllByEnabledTrue();
    
    /**
     * Find roles by enabled status.
     * 
     * @param enabled the enabled status
     * @return Set of roles with specified status
     */
    Set<Role> findAllByEnabled(Boolean enabled);
    
    /**
     * Find roles with specific permission.
     * 
     * @param permissionName the permission name
     * @return Set of roles that have the specified permission
     */
    @Query("SELECT r FROM Role r JOIN r.permissions p WHERE p.name = :permissionName AND r.enabled = true")
    Set<Role> findRolesWithPermission(@Param("permissionName") String permissionName);
    
    /**
     * Find roles by name containing specified text (case-insensitive).
     * 
     * @param name the name fragment
     * @return Set of roles with names containing the specified text
     */
    Set<Role> findByNameContainingIgnoreCase(String name);
}
