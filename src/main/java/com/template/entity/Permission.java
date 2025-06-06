package com.template.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Permission entity representing specific permissions in the application.
 * Used for fine-grained access control in combination with roles.
 */
@Entity
@Table(name = "permissions", indexes = {
    @Index(name = "idx_permission_name", columnList = "name", unique = true)
})
public class Permission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @NotBlank(message = "Permission name is required")
    @Size(min = 2, max = 100, message = "Permission name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;
    
    @Size(max = 255, message = "Description must not exceed 255 characters")
    @Column(name = "description")
    private String description;
    
    @Size(max = 50, message = "Resource must not exceed 50 characters")
    @Column(name = "resource", length = 50)
    private String resource;
    
    @Size(max = 50, message = "Action must not exceed 50 characters")
    @Column(name = "action", length = 50)
    private String action;
    
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;
    
    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Default constructor for JPA.
     */
    public Permission() {
    }
    
    /**
     * Constructor with required fields.
     * 
     * @param name the permission name
     */
    public Permission(String name) {
        this.name = name;
    }
    
    /**
     * Constructor with name and description.
     * 
     * @param name the permission name
     * @param description the permission description
     */
    public Permission(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    /**
     * Constructor with all fields.
     * 
     * @param name the permission name
     * @param description the permission description
     * @param resource the resource type
     * @param action the action type
     */
    public Permission(String name, String description, String resource, String action) {
        this.name = name;
        this.description = description;
        this.resource = resource;
        this.action = action;
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getResource() {
        return resource;
    }
    
    public void setResource(String resource) {
        this.resource = resource;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public Boolean getEnabled() {
        return enabled;
    }
    
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    public Set<Role> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
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
    
    // Helper methods
    
    /**
     * Get the full permission string (resource:action format).
     * 
     * @return the permission string
     */
    public String getFullPermission() {
        if (resource != null && action != null) {
            return resource + ":" + action;
        }
        return name;
    }
    
    /**
     * Check if this permission matches a resource and action.
     * 
     * @param resource the resource to check
     * @param action the action to check
     * @return true if matches
     */
    public boolean matches(String resource, String action) {
        return Objects.equals(this.resource, resource) && Objects.equals(this.action, action);
    }
    
    // equals, hashCode, and toString
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission permission = (Permission) o;
        return Objects.equals(name, permission.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    
    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", resource='" + resource + '\'' +
                ", action='" + action + '\'' +
                ", enabled=" + enabled +
                ", createdAt=" + createdAt +
                '}';
    }
}
