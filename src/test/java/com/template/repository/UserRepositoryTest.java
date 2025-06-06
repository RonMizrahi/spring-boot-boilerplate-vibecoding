package com.template.repository;

import com.template.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for UserRepository.
 * Uses H2 in-memory database for testing JPA operations.
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository Tests")
class UserRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "test@example.com", "password123");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
    }
    
    @Test
    @DisplayName("Should save and find user by ID")
    void shouldSaveAndFindUserById() {
        // Given
        var savedUser = entityManager.persistAndFlush(testUser);
        
        // When
        var foundUser = userRepository.findById(savedUser.getId());
        
        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
    }
    
    @Test
    @DisplayName("Should find user by username")
    void shouldFindUserByUsername() {
        // Given
        entityManager.persistAndFlush(testUser);
        
        // When
        var foundUser = userRepository.findByUsername("testuser");
        
        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
    }
    
    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        // Given
        entityManager.persistAndFlush(testUser);
        
        // When
        var foundUser = userRepository.findByEmail("test@example.com");
        
        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
    }
    
    @Test
    @DisplayName("Should find user by username or email")
    void shouldFindUserByUsernameOrEmail() {
        // Given
        entityManager.persistAndFlush(testUser);
        
        // When
        var foundByUsername = userRepository.findByUsernameOrEmail("testuser", "nonexistent@example.com");
        var foundByEmail = userRepository.findByUsernameOrEmail("nonexistent", "test@example.com");
        
        // Then
        assertThat(foundByUsername).isPresent();
        assertThat(foundByEmail).isPresent();
        assertThat(foundByUsername.get().getId()).isEqualTo(foundByEmail.get().getId());
    }
    
    @Test
    @DisplayName("Should check if user exists by username")
    void shouldCheckIfUserExistsByUsername() {
        // Given
        entityManager.persistAndFlush(testUser);
        
        // When & Then
        assertThat(userRepository.existsByUsername("testuser")).isTrue();
        assertThat(userRepository.existsByUsername("nonexistent")).isFalse();
    }
    
    @Test
    @DisplayName("Should check if user exists by email")
    void shouldCheckIfUserExistsByEmail() {
        // Given
        entityManager.persistAndFlush(testUser);
        
        // When & Then
        assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("nonexistent@example.com")).isFalse();
    }
    
    @Test
    @DisplayName("Should find enabled users")
    void shouldFindEnabledUsers() {
        // Given
        var enabledUser = testUser;
        var disabledUser = new User("disabled", "disabled@example.com", "password123");
        disabledUser.setEnabled(false);
        
        entityManager.persistAndFlush(enabledUser);
        entityManager.persistAndFlush(disabledUser);
        
        // When
        var enabledUsers = userRepository.findByEnabledTrue();
        
        // Then
        assertThat(enabledUsers).hasSize(1);
        assertThat(enabledUsers.get(0).getUsername()).isEqualTo("testuser");
    }
    
    @Test
    @DisplayName("Should find disabled users")
    void shouldFindDisabledUsers() {
        // Given
        var enabledUser = testUser;
        var disabledUser = new User("disabled", "disabled@example.com", "password123");
        disabledUser.setEnabled(false);
        
        entityManager.persistAndFlush(enabledUser);
        entityManager.persistAndFlush(disabledUser);
        
        // When
        var disabledUsers = userRepository.findByEnabledFalse();
        
        // Then
        assertThat(disabledUsers).hasSize(1);
        assertThat(disabledUsers.get(0).getUsername()).isEqualTo("disabled");
    }
    
    @Test
    @DisplayName("Should find users created after specific date")
    void shouldFindUsersCreatedAfterDate() {
        // Given
        var pastDate = LocalDateTime.now().minusDays(1);
        var futureDate = LocalDateTime.now().plusDays(1);
        
        entityManager.persistAndFlush(testUser);
        
        // When
        var usersAfterPast = userRepository.findByCreatedAtAfter(pastDate);
        var usersAfterFuture = userRepository.findByCreatedAtAfter(futureDate);
        
        // Then
        assertThat(usersAfterPast).hasSize(1);
        assertThat(usersAfterFuture).isEmpty();
    }
    
    @Test
    @DisplayName("Should find users by username pattern with pagination")
    void shouldFindUsersByUsernamePatternWithPagination() {
        // Given
        var user1 = new User("testuser1", "test1@example.com", "password123");
        var user2 = new User("testuser2", "test2@example.com", "password123");
        var user3 = new User("otheruser", "other@example.com", "password123");
        
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        entityManager.persistAndFlush(user3);
        
        var pageable = PageRequest.of(0, 10);
        
        // When
        var testUsers = userRepository.findByUsernameContainingIgnoreCase("test", pageable);
        var allUsers = userRepository.findByUsernameContainingIgnoreCase("user", pageable);
        
        // Then
        assertThat(testUsers.getContent()).hasSize(2);
        assertThat(allUsers.getContent()).hasSize(3);
    }
    
    @Test
    @DisplayName("Should find users by email pattern with pagination")
    void shouldFindUsersByEmailPatternWithPagination() {
        // Given
        var user1 = new User("user1", "test1@example.com", "password123");
        var user2 = new User("user2", "test2@example.com", "password123");
        var user3 = new User("user3", "user@other.com", "password123");
        
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        entityManager.persistAndFlush(user3);
        
        var pageable = PageRequest.of(0, 10);
        
        // When
        var exampleUsers = userRepository.findByEmailContainingIgnoreCase("example", pageable);
        var testUsers = userRepository.findByEmailContainingIgnoreCase("test", pageable);
        
        // Then
        assertThat(exampleUsers.getContent()).hasSize(2);
        assertThat(testUsers.getContent()).hasSize(2);
    }
    
    @Test
    @DisplayName("Should find users by name containing")
    void shouldFindUsersByNameContaining() {
        // Given
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        
        var user2 = new User("user2", "user2@example.com", "password123");
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        
        var user3 = new User("user3", "user3@example.com", "password123");
        user3.setFirstName("Bob");
        user3.setLastName("Johnson");
        
        entityManager.persistAndFlush(testUser);
        entityManager.persistAndFlush(user2);
        entityManager.persistAndFlush(user3);
        
        // When
        var johnUsers = userRepository.findByNameContaining("john");
        var jUsers = userRepository.findByNameContaining("j");
        
        // Then
        assertThat(johnUsers).hasSize(2); // John Doe and Bob Johnson
        assertThat(jUsers).hasSize(3); // John, Jane, Johnson
    }
    
    @Test
    @DisplayName("Should find active users")
    void shouldFindActiveUsers() {
        // Given
        var activeUser = testUser;
        
        var lockedUser = new User("locked", "locked@example.com", "password123");
        lockedUser.setAccountNonLocked(false);
        
        var expiredUser = new User("expired", "expired@example.com", "password123");
        expiredUser.setAccountNonExpired(false);
        
        var disabledUser = new User("disabled", "disabled@example.com", "password123");
        disabledUser.setEnabled(false);
        
        entityManager.persistAndFlush(activeUser);
        entityManager.persistAndFlush(lockedUser);
        entityManager.persistAndFlush(expiredUser);
        entityManager.persistAndFlush(disabledUser);
        
        // When
        var activeUsers = userRepository.findActiveUsers();
        
        // Then
        assertThat(activeUsers).hasSize(1);
        assertThat(activeUsers.get(0).getUsername()).isEqualTo("testuser");
    }
    
    @Test
    @DisplayName("Should count users created between dates")
    void shouldCountUsersCreatedBetweenDates() {
        // Given
        var now = LocalDateTime.now();
        var startDate = now.minusDays(1);
        var endDate = now.plusDays(1);
        
        entityManager.persistAndFlush(testUser);
        
        // When
        var count = userRepository.countUsersCreatedBetween(startDate, endDate);
        var countOutsideRange = userRepository.countUsersCreatedBetween(now.plusDays(2), now.plusDays(3));
        
        // Then
        assertThat(count).isEqualTo(1L);
        assertThat(countOutsideRange).isEqualTo(0L);
    }
    
    @Test
    @DisplayName("Should find users by email domain")
    void shouldFindUsersByEmailDomain() {
        // Given
        var user1 = new User("user1", "user1@example.com", "password123");
        var user2 = new User("user2", "user2@example.com", "password123");
        var user3 = new User("user3", "user3@other.com", "password123");
        
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        entityManager.persistAndFlush(user3);
        
        // When
        var exampleUsers = userRepository.findByEmailDomain("example.com");
        var otherUsers = userRepository.findByEmailDomain("other.com");
        
        // Then
        assertThat(exampleUsers).hasSize(2);
        assertThat(otherUsers).hasSize(1);
    }
}
