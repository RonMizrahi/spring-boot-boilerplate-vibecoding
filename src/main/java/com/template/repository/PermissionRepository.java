package com.template.repository;

import com.template.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * Repository interface for Permission entity operations.
 * Provides database access methods for permission management.
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    /**
     * Find permission by name.
     * 
     * @param name the permission name
     * @return Optional containing the permission if found
     */
    Optional<Permission> findByName(String name);
    
    /**
     * Find permission by name ignoring case.
     * 
     * @param name the permission name
     * @return Optional containing the permission if found
     */
    Optional<Permission> findByNameIgnoreCase(String name);
    
    /**
     * Check if permission exists by name.
     * 
     * @param name the permission name
     * @return true if permission exists
     */
    boolean existsByName(String name);
    
    /**
     * Check if permission exists by name ignoring case.
     * 
     * @param name the permission name
     * @return true if permission exists
     */
    boolean existsByNameIgnoreCase(String name);
    
    /**
     * Find all enabled permissions.
     * 
     * @return Set of enabled permissions
     */
    Set<Permission> findAllByEnabledTrue();
    
    /**
     * Find permissions by enabled status.
     * 
     * @param enabled the enabled status
     * @return Set of permissions with specified status
     */
    Set<Permission> findAllByEnabled(Boolean enabled);
    
    /**
     * Find permissions by resource.
     * 
     * @param resource the resource name
     * @return Set of permissions for the specified resource
     */
    Set<Permission> findByResource(String resource);
    
    /**
     * Find permissions by action.
     * 
     * @param action the action name
     * @return Set of permissions for the specified action
     */
    Set<Permission> findByAction(String action);
    
    /**
     * Find permissions by resource and action.
     * 
     * @param resource the resource name
     * @param action the action name
     * @return Set of permissions for the specified resource and action
     */
    Set<Permission> findByResourceAndAction(String resource, String action);
    
    /**
     * Find permissions assigned to a specific role.
     * 
     * @param roleName the role name
     * @return Set of permissions assigned to the role
     */
    @Query("SELECT p FROM Permission p JOIN p.roles r WHERE r.name = :roleName AND p.enabled = true")
    Set<Permission> findPermissionsByRole(@Param("roleName") String roleName);
    
    /**
     * Find permissions by name containing specified text (case-insensitive).
     * 
     * @param name the name fragment
     * @return Set of permissions with names containing the specified text
     */
    Set<Permission> findByNameContainingIgnoreCase(String name);
}
