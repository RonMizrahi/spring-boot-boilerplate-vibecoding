package com.template.service;

import com.template.entity.User;
import com.template.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService.
 * Uses Mockito to mock UserRepository dependencies.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private KafkaEventPublisher kafkaEventPublisher;
    
    private UserService userService;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, Optional.of(kafkaEventPublisher));
        
        testUser = new User("testuser", "test@example.com", "password123");
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
    }
    
    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // When
        var createdUser = userService.createUser(testUser);
        
        // Then
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getUsername()).isEqualTo("testuser");
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository).save(testUser);
    }
    
    @Test
    @DisplayName("Should throw exception when creating user with existing username")
    void shouldThrowExceptionWhenCreatingUserWithExistingUsername() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> userService.createUser(testUser))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Username 'testuser' already exists");
        
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should throw exception when creating user with existing email")
    void shouldThrowExceptionWhenCreatingUserWithExistingEmail() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> userService.createUser(testUser))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Email 'test@example.com' already exists");
        
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // When
        var updatedUser = userService.updateUser(testUser);
        
        // Then
        assertThat(updatedUser).isNotNull();
        verify(userRepository).existsById(1L);
        verify(userRepository).save(testUser);
    }
    
    @Test
    @DisplayName("Should throw exception when updating user with null ID")
    void shouldThrowExceptionWhenUpdatingUserWithNullId() {
        // Given
        testUser.setId(null);
        
        // When & Then
        assertThatThrownBy(() -> userService.updateUser(testUser))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User ID cannot be null for update");
        
        verify(userRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void shouldThrowExceptionWhenUpdatingNonExistentUser() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> userService.updateUser(testUser))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User with ID 1 not found");
        
        verify(userRepository).existsById(1L);
        verify(userRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should find user by ID")
    void shouldFindUserById() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        // When
        var foundUser = userService.findById(1L);
        
        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
        verify(userRepository).findById(1L);
    }
    
    @Test
    @DisplayName("Should find user by username")
    void shouldFindUserByUsername() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        // When
        var foundUser = userService.findByUsername("testuser");
        
        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
        verify(userRepository).findByUsername("testuser");
    }
    
    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        
        // When
        var foundUser = userService.findByEmail("test@example.com");
        
        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
        verify(userRepository).findByEmail("test@example.com");
    }
    
    @Test
    @DisplayName("Should find user by username or email")
    void shouldFindUserByUsernameOrEmail() {
        // Given
        when(userRepository.findByUsernameOrEmail("testuser", "testuser")).thenReturn(Optional.of(testUser));
        
        // When
        var foundUser = userService.findByUsernameOrEmail("testuser");
        
        // Then
        assertThat(foundUser).isPresent();
        verify(userRepository).findByUsernameOrEmail("testuser", "testuser");
    }
    
    @Test
    @DisplayName("Should find all users with pagination")
    void shouldFindAllUsersWithPagination() {
        // Given
        var pageable = PageRequest.of(0, 10);
        var users = List.of(testUser);
        var page = new PageImpl<>(users, pageable, 1);
        when(userRepository.findAll(pageable)).thenReturn(page);
        
        // When
        var result = userService.findAll(pageable);
        
        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("testuser");
        verify(userRepository).findAll(pageable);
    }
    
    @Test
    @DisplayName("Should find enabled users")
    void shouldFindEnabledUsers() {
        // Given
        var users = List.of(testUser);
        when(userRepository.findByEnabledTrue()).thenReturn(users);
        
        // When
        var result = userService.findEnabledUsers();
        
        // Then
        assertThat(result).hasSize(1);
        verify(userRepository).findByEnabledTrue();
    }
    
    @Test
    @DisplayName("Should find active users")
    void shouldFindActiveUsers() {
        // Given
        var users = List.of(testUser);
        when(userRepository.findActiveUsers()).thenReturn(users);
        
        // When
        var result = userService.findActiveUsers();
        
        // Then
        assertThat(result).hasSize(1);
        verify(userRepository).findActiveUsers();
    }
    
    @Test
    @DisplayName("Should search users by username")
    void shouldSearchUsersByUsername() {
        // Given
        var pageable = PageRequest.of(0, 10);
        var users = List.of(testUser);
        var page = new PageImpl<>(users, pageable, 1);
        when(userRepository.findByUsernameContainingIgnoreCase("test", pageable)).thenReturn(page);
        
        // When
        var result = userService.searchByUsername("test", pageable);
        
        // Then
        assertThat(result.getContent()).hasSize(1);
        verify(userRepository).findByUsernameContainingIgnoreCase("test", pageable);
    }
    
    @Test
    @DisplayName("Should search users by email")
    void shouldSearchUsersByEmail() {
        // Given
        var pageable = PageRequest.of(0, 10);
        var users = List.of(testUser);
        var page = new PageImpl<>(users, pageable, 1);
        when(userRepository.findByEmailContainingIgnoreCase("example", pageable)).thenReturn(page);
        
        // When
        var result = userService.searchByEmail("example", pageable);
        
        // Then
        assertThat(result.getContent()).hasSize(1);
        verify(userRepository).findByEmailContainingIgnoreCase("example", pageable);
    }
    
    @Test
    @DisplayName("Should search users by name")
    void shouldSearchUsersByName() {
        // Given
        var users = List.of(testUser);
        when(userRepository.findByNameContaining("john")).thenReturn(users);
        
        // When
        var result = userService.searchByName("john");
        
        // Then
        assertThat(result).hasSize(1);
        verify(userRepository).findByNameContaining("john");
    }
    
    @Test
    @DisplayName("Should enable user successfully")
    void shouldEnableUserSuccessfully() {
        // Given
        testUser.setEnabled(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // When
        userService.enableUser(1L);
        
        // Then
        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
        assertThat(testUser.getEnabled()).isTrue();
    }
    
    @Test
    @DisplayName("Should throw exception when enabling non-existent user")
    void shouldThrowExceptionWhenEnablingNonExistentUser() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> userService.enableUser(1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User with ID 1 not found");
        
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should disable user successfully")
    void shouldDisableUserSuccessfully() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // When
        userService.disableUser(1L);
        
        // Then
        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
        assertThat(testUser.getEnabled()).isFalse();
    }
    
    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        
        // When
        userService.deleteUser(1L);
        
        // Then
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }
    
    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void shouldThrowExceptionWhenDeletingNonExistentUser() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> userService.deleteUser(1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User with ID 1 not found");
        
        verify(userRepository).existsById(1L);
        verify(userRepository, never()).deleteById(any());
    }
    
    @Test
    @DisplayName("Should count users created between dates")
    void shouldCountUsersCreatedBetweenDates() {
        // Given
        var startDate = LocalDateTime.now().minusDays(1);
        var endDate = LocalDateTime.now();
        when(userRepository.countUsersCreatedBetween(startDate, endDate)).thenReturn(5L);
        
        // When
        var count = userService.countUsersCreatedBetween(startDate, endDate);
        
        // Then
        assertThat(count).isEqualTo(5L);
        verify(userRepository).countUsersCreatedBetween(startDate, endDate);
    }
    
    @Test
    @DisplayName("Should check if username exists")
    void shouldCheckIfUsernameExists() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        
        // When
        var exists = userService.existsByUsername("testuser");
        
        // Then
        assertThat(exists).isTrue();
        verify(userRepository).existsByUsername("testuser");
    }
    
    @Test
    @DisplayName("Should check if email exists")
    void shouldCheckIfEmailExists() {
        // Given
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        
        // When
        var exists = userService.existsByEmail("test@example.com");
        
        // Then
        assertThat(exists).isTrue();
        verify(userRepository).existsByEmail("test@example.com");
    }
}
