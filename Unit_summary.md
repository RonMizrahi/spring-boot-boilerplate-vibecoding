## Unit 5 - MongoDB Repository Cleanup ##
**Date: June 6, 2025**

### ✅ Completed Tasks:
1. **Repository Structure Reorganization**
   - Moved UserProfileRepository to `com.template.repository.mongo` package
   - Moved UserProfileRepositoryTest to `com.template.repository.mongo` package  
   - Removed old duplicate files from main repository directory
   - Clean separation between JPA repositories and MongoDB repositories

2. **Configuration Updates**
   - Updated MongoConfig to scan `com.template.repository.mongo` package
   - Fixed all import references in UserProfileService and UserProfileServiceTest
   - Removed Java preview features from Maven configuration (not needed for Java 21)

3. **Embedded MongoDB Cleanup**
   - Removed `--enable-preview` compiler arguments from pom.xml
   - Confirmed no embedded MongoDB dependencies present
   - Using standard Spring Data MongoDB for operations

4. **Test Fixes**
   - Fixed UserProfileServiceTest import statements
   - Corrected date handling (LocalDateTime vs LocalDate)
   - Updated method calls to match UserProfile API (setIsPublic, setUserPreferences)

### 📁 Final Directory Structure:
```
src/main/java/com/template/repository/
├── mongo/UserProfileRepository.java     (MongoDB)
└── UserRepository.java                  (JPA)

src/test/java/com/template/repository/
├── mongo/UserProfileRepositoryTest.java (MongoDB tests)
└── UserRepositoryTest.java              (JPA tests)
```

### 🔧 Technical Details:
- Java 21 best practices maintained
- Spring Boot 3.3.0 compatibility ensured
- No embedded dependencies for testing (uses standard Spring test configurations)
- All imports and package references updated correctly

**Commit:** `8ea8351` - MongoDB cleanup and reorganization complete

---

## Unit 6 - Redis Integration and Caching ##
**Date: June 6, 2025**

### ✅ Completed Tasks:
1. **Redis Configuration Setup**
   - Created `RedisConfig.java` with comprehensive Redis and caching configuration
   - Configured `RedisTemplate` with JSON serialization using Jackson
   - Set up `CacheManager` with multiple cache configurations and TTL settings
   - Fixed deprecated `setObjectMapper()` usage with new Spring Data Redis 3.x API

2. **Cache Integration in Services**
   - Added `@Cacheable`, `@CachePut`, and `@CacheEvict` annotations to `UserService`
   - Added caching to `UserProfileService` for MongoDB operations
   - Configured cache keys and TTL for optimal performance:
     - `users` cache: 15 minutes TTL
     - `userProfiles` cache: 10 minutes TTL
     - `sessions` cache: 1 hour TTL

3. **Direct Redis Operations Service**
   - Created `RedisService.java` with utility methods for direct Redis operations
   - Implemented methods for: set/get, delete, expiration, increment, sets, lists
   - Added proper error handling and logging
   - Made service conditional on Redis configuration presence

4. **Testing Infrastructure**
   - Created `RedisServiceTest.java` with comprehensive unit tests
   - Created `RedisConfigTest.java` for configuration validation
   - Used mocked `RedisTemplate` to avoid requiring actual Redis instance
   - All tests pass and validate Redis functionality

### 🔧 Technical Details:
- **JSON Serialization**: Uses Jackson with Java Time module for LocalDateTime support
- **Cache Strategy**: Implements read-through and write-through caching patterns
- **Error Handling**: Graceful fallback when Redis is unavailable
- **Conditional Configuration**: Only activates when `spring.data.redis.host` is configured
- **Performance**: Multiple cache configurations with appropriate TTL settings

### 📁 New Files Created:
```
src/main/java/com/template/config/RedisConfig.java
src/main/java/com/template/service/RedisService.java
src/test/java/com/template/config/RedisConfigTest.java
src/test/java/com/template/service/RedisServiceTest.java
```

### 🚀 Cache Integration Points:
- `UserService.findById()` - Cacheable by user ID
- `UserService.findByUsername()` - Cacheable by username
- `UserService.createUser()` - Cache put on creation
- `UserService.updateUser()` - Cache put with eviction of old username cache
- `UserService.deleteUser()` - Cache eviction
- `UserProfileService.findById()` - Cacheable by profile ID
- `UserProfileService.findByUserId()` - Cacheable by user ID with prefix
- `UserProfileService.createUserProfile()` - Cache put on creation
- `UserProfileService.updateUserProfile()` - Cache put with eviction
- `UserProfileService.deleteById()` - Cache eviction

**Commit:** `c73e2a7` - Redis integration and caching implementation complete

---

## Unit 7 - Kafka Integration and Event-Driven Architecture ##
**Date: June 6, 2025**

### ✅ Completed Tasks:
1. **Event Classes Creation**
   - Created `BaseEvent.java` as abstract base class for all events
   - Created `UserCreatedEvent.java` for user creation notifications
   - Created `UserUpdatedEvent.java` for user update notifications  
   - Created `UserDeletedEvent.java` for user deletion notifications
   - All events use Jackson annotations for JSON serialization
   - Support for LocalDateTime with proper JSON handling

2. **Kafka Configuration Setup**
   - Created `KafkaConfig.java` with comprehensive producer/consumer configuration
   - Configured separate factories for String and JSON message types
   - Implemented error handling with `ErrorHandlingDeserializer`
   - Set up manual acknowledgment for reliable message processing
   - Configured trusted packages for JSON deserialization security
   - Added idempotent producers with retries and "all" acknowledgment

3. **Event Publishing Service**
   - Created `KafkaEventPublisher.java` for publishing events to Kafka topics
   - Implemented methods for both JSON events and string messages
   - Added topic constants: `user-events`, `notifications`, `audit-events`
   - Comprehensive error handling with logging and CompletableFuture returns
   - Conditional loading based on Kafka configuration presence

4. **Event Consumption Service**
   - Created `KafkaEventConsumer.java` with `@KafkaListener` annotations
   - Separate handlers for user events and notifications
   - Manual acknowledgment to prevent message loss
   - Comprehensive error handling with logging
   - Processing methods for different event types (placeholder implementations)

5. **UserService Integration**
   - Integrated Kafka event publishing into `UserService`
   - Added optional `KafkaEventPublisher` dependency with graceful degradation
   - Events published on: user creation, update, and deletion
   - Updated constructor to accept optional KafkaEventPublisher
   - All events include proper metadata (eventId, timestamp, source)

6. **Testing Infrastructure**
   - Created `KafkaEventPublisherTest.java` with comprehensive unit tests
   - Created `KafkaEventConsumerTest.java` for consumer testing
   - Created `KafkaConfigTest.java` for configuration validation
   - Updated `UserServiceTest.java` to handle new constructor signature
   - All tests use mocked Kafka templates to avoid requiring actual Kafka instance

### 🔧 Technical Details:
- **Event-Driven Architecture**: Implements publish-subscribe pattern for user operations
- **JSON Serialization**: Uses Jackson with JavaTimeModule for LocalDateTime support
- **Error Handling**: Graceful fallback when Kafka is unavailable
- **Reliable Processing**: Manual acknowledgment prevents message loss
- **Security**: Trusted packages configuration for JSON deserialization
- **Performance**: Idempotent producers with optimized retry logic

### 📁 New Files Created:
```
src/main/java/com/template/event/
├── BaseEvent.java
├── UserCreatedEvent.java
├── UserUpdatedEvent.java
└── UserDeletedEvent.java

src/main/java/com/template/config/KafkaConfig.java
src/main/java/com/template/service/KafkaEventPublisher.java
src/main/java/com/template/service/KafkaEventConsumer.java

src/test/java/com/template/config/KafkaConfigTest.java
src/test/java/com/template/service/KafkaEventPublisherTest.java
src/test/java/com/template/service/KafkaEventConsumerTest.java
```

### 🚀 Event Integration Points:
- `UserService.createUser()` - Publishes UserCreatedEvent to user-events topic
- `UserService.updateUser()` - Publishes UserUpdatedEvent to user-events topic
- `UserService.deleteUser()` - Publishes UserDeletedEvent to user-events topic
- Event consumers ready for processing notifications, analytics, and audit trails
- Supports both direct Kafka operations and convenience methods

### 📋 Kafka Topics Configuration:
- **user-events**: JSON events for user lifecycle operations
- **notifications**: String messages for user notifications
- **audit-events**: JSON events for audit and compliance tracking

### ⚙️ Configuration:
- **Conditional Loading**: Only activates when `spring.kafka.bootstrap-servers` is configured
- **Development Config**: Pre-configured in `application-dev.yml` for local development
- **Producer Settings**: Idempotent with retries and full acknowledgment
- **Consumer Settings**: Manual acknowledgment with earliest offset reset

**Commit:** `6a4e83e` - Kafka integration and event-driven architecture complete

---

## Unit 8 - Spring Security Configuration ##
**Date: [Current Date]**

### ✅ Completed Tasks:
1. **SecurityConfig Implementation**
   - Created `src/main/java/com/template/config/SecurityConfig.java`
   - Configured SecurityFilterChain with public endpoints (`/api/health`, `/api/auth/**`)
   - Set up HTTP Basic authentication for protected endpoints
   - Added security headers (frame options, content type options, XSS protection)
   - Configured stateless session management for REST API
   - Added BCryptPasswordEncoder bean for password hashing

2. **Custom UserDetailsService Implementation**
   - Created `src/main/java/com/template/service/UserDetailsServiceImpl.java`
   - Implemented UserDetailsService interface for Spring Security authentication
   - Added support for login with both username and email
   - Mapped User entity fields to Spring Security UserDetails
   - Set default ROLE_USER authority (to be enhanced in Unit 10)
   - Added proper error handling for user not found scenarios

3. **Authentication Configuration**
   - Created AuthenticationManager bean with DaoAuthenticationProvider
   - Integrated UserDetailsServiceImpl with password encoder
   - Configured for database-driven authentication

### 🔧 Technical Implementation:
- **Spring Security 6.x**: Used modern configuration approach with SecurityFilterChain
- **Password Security**: BCrypt encoder with default strength (10 rounds)
- **Session Management**: Stateless for REST API compatibility
- **Security Headers**: Comprehensive protection against common web vulnerabilities
- **Flexible Login**: Users can authenticate with username or email
- **Database Integration**: Full integration with existing User entity and UserRepository

### 📁 Files Created:
```
src/main/java/com/template/config/SecurityConfig.java
src/main/java/com/template/service/UserDetailsServiceImpl.java
```

### 🔐 Security Features:
- Public health and authentication endpoints
- Protected API endpoints requiring authentication
- Secure password hashing with BCrypt
- XSS and clickjacking protection
- Stateless authentication suitable for microservices

**Status:** ✅ Unit 8 Complete - Basic Spring Security configuration implemented and verified

---

## Unit 9 - JWT Token Generation and Validation ##
**Date: June 6, 2025**

### ✅ Completed Tasks:
1. **JWT Utility Implementation**
   - Created `src/main/java/com/template/security/JwtUtils.java`
   - JWT token generation from authentication and username
   - Token validation with comprehensive error handling
   - Username extraction from valid tokens
   - Token expiration checking functionality
   - Configurable secret key and expiration time

2. **JWT Authentication Filter**
   - Created `src/main/java/com/template/security/JwtAuthenticationFilter.java`
   - Custom filter extending OncePerRequestFilter
   - Authorization header parsing (Bearer token format)
   - Automatic user authentication from valid JWT tokens
   - Integration with Spring Security context
   - Proper error handling and logging

3. **Authentication Controller**
   - Created `src/main/java/com/template/controller/AuthController.java`
   - POST `/api/auth/login` endpoint for user authentication
   - POST `/api/auth/validate` endpoint for token validation
   - GET `/api/auth/me` endpoint for current user info
   - Comprehensive error handling with proper HTTP status codes
   - JWT token response with user details

4. **Security Configuration Updates**
   - Updated SecurityConfig to integrate JWT authentication filter
   - Changed session management to STATELESS for JWT compatibility
   - Removed HTTP Basic and form login (replaced with JWT)
   - Added JWT filter before UsernamePasswordAuthenticationFilter
   - Maintained public endpoints for authentication and health checks

5. **JWT Configuration**
   - Added JWT configuration properties to application.yml
   - Configurable secret key and token expiration
   - Default 24-hour token expiration
   - Secure JWT secret key for signing

### 🔧 Technical Implementation:
- **JJWT Library**: Modern JWT implementation with version 0.12.5
- **Security Integration**: Full Spring Security integration with custom filter
- **Stateless Authentication**: No server-side session storage
- **Bearer Token Format**: Standard Authorization header implementation
- **Error Handling**: Comprehensive validation and logging
- **Flexible Login**: Support for username/email authentication from Unit 8

### 📁 Files Created/Modified:
```
src/main/java/com/template/security/JwtUtils.java
src/main/java/com/template/security/JwtAuthenticationFilter.java
src/main/java/com/template/controller/AuthController.java
src/main/java/com/template/config/SecurityConfig.java (updated)
src/main/resources/application.yml (updated)
pom.xml (added JJWT dependencies)
```

### 🔐 JWT Features:
- **Token Generation**: Creates signed JWT tokens with username and expiration
- **Token Validation**: Validates signature, expiration, and format
- **Automatic Authentication**: Filter processes tokens on every request
- **RESTful Endpoints**: Login, validation, and user info endpoints
- **Security Headers**: Proper Bearer token format support
- **Error Responses**: Clear error messages for invalid credentials/tokens

### 🎯 API Endpoints:
- `POST /api/auth/login` - User login with username/password, returns JWT
- `POST /api/auth/validate` - Validate JWT token
- `GET /api/auth/me` - Get current authenticated user info
- All other endpoints now require valid JWT token in Authorization header

**Status:** ✅ Unit 9 Complete - JWT token generation and validation implemented successfully

---

## Unit 10 - Role-Based Authorization (RBAC) ##
**Date: June 6, 2025**

### ✅ Completed Tasks:
1. **RBAC Entity Model**
   - Created `Permission.java` entity with resource/action fields
   - Created `Role.java` entity with many-to-many permissions relationship
   - Updated `User.java` entity with many-to-many roles relationship
   - Added helper methods for roles and permissions management

2. **Repository Layer**
   - Created `RoleRepository.java` with role management methods
   - Created `PermissionRepository.java` with permission management methods
   - Added query methods for finding roles by permissions and vice versa
   - Integrated repositories with existing UserRepository

3. **Security Integration**
   - Updated `UserDetailsServiceImpl.java` to map roles/permissions to Spring Security authorities
   - Enhanced `AuthorizationService.java` with programmatic role/permission checks
   - Added role and permission validation methods
   - Implemented security event logging

4. **Method-Level Security**
   - Added `@PreAuthorize` annotations to controller methods
   - Protected admin endpoints with `hasRole('ADMIN')` checks
   - Protected user endpoints with `isAuthenticated()` checks
   - Created detailed health endpoint for admin access only

### 📁 Files Created/Modified:
```
src/main/java/com/template/entity/Permission.java (new)
src/main/java/com/template/entity/Role.java (new)
src/main/java/com/template/entity/User.java (updated - added roles)
src/main/java/com/template/repository/RoleRepository.java (new)
src/main/java/com/template/repository/PermissionRepository.java (new)
src/main/java/com/template/service/UserDetailsServiceImpl.java (updated)
src/main/java/com/template/security/AuthorizationService.java (updated)
src/main/java/com/template/controller/AuthController.java (updated - security annotations)
src/main/java/com/template/controller/HealthController.java (updated - admin endpoint)
src/main/java/com/template/config/SecurityConfig.java (already had @EnableMethodSecurity)
```

### 🔐 RBAC Features:
- **Hierarchical Permissions**: Role -> Permissions -> Actions on Resources
- **Many-to-Many Relationships**: Users can have multiple roles, roles can have multiple permissions
- **Method-Level Security**: `@PreAuthorize` annotations for fine-grained access control
- **Programmatic Checks**: AuthorizationService for custom authorization logic
- **Spring Security Integration**: Authorities automatically mapped from roles and permissions
- **Admin vs User Access**: Different permission levels for different user types

### 🎯 Security Annotations Added:
- `@PreAuthorize("isAuthenticated()")` - Requires valid authentication
- `@PreAuthorize("hasRole('ADMIN')")` - Requires ADMIN role
- `@PreAuthorize("hasPermission('USER', 'WRITE')")` - Custom permission checks
- Method-level security enabled globally via `@EnableMethodSecurity`

### 📊 Entity Relationships:
```
User (1) ←→ (N) Role (N) ←→ (N) Permission
- Users can have multiple roles
- Roles can have multiple permissions  
- Permissions define resource-action pairs
- Authorities = ROLE_[role] + [permission] formats
```

### 🔧 Technical Implementation:
- **Database Schema**: Proper indexes on join tables for performance
- **Validation**: Bean validation on all entity fields
- **Timestamps**: CreatedAt/UpdatedAt for audit trails
- **Enabled Flags**: Soft disable for roles and permissions
- **Helper Methods**: Convenient role/permission management on User entity

**Status:** ✅ Unit 10 Complete - Role-based authorization with method-level security implemented successfully

---

## Unit 11 - Comprehensive REST API Layer ##
**Date: June 6, 2025**

### ✅ Completed Tasks:
1. **Data Transfer Objects (DTOs) Creation**
   - Created `UserDTO.java` for User entity API representation
   - Created `UserProfileDTO.java` for UserProfile document API representation
   - Created `CreateUserRequestDTO.java` for user creation requests with password field
   - Added comprehensive validation annotations and JSON formatting
   - Separated sensitive data (passwords) from response DTOs

2. **UserController Implementation**
   - Created comprehensive REST API for User entity operations
   - Implemented full CRUD operations with proper HTTP status codes
   - Added role-based authorization with `@PreAuthorize` annotations
   - Implemented pagination and sorting for list operations
   - Added search functionality (by username, email, name)
   - Added user management operations (enable/disable/delete)
   - Added utility endpoints (existence checks, count statistics)

3. **UserProfileController Implementation**
   - Created comprehensive REST API for UserProfile document operations
   - Implemented full CRUD operations with proper HTTP status codes
   - Added privacy-aware operations (public vs private profiles)
   - Implemented advanced search functionality (location, bio, age range, preferences)
   - Added specialized endpoints (recent profiles, profiles with website)
   - Added analytics endpoints (count by location)

4. **Security Integration**
   - Admin-only operations for user creation and system management
   - User can access their own profile data and update operations
   - Public endpoints for viewing public profiles
   - Authenticated endpoints for searching and discovery
   - Proper authorization checks in UserProfileController

5. **DTO Mapping and Conversion**
   - Entity-to-DTO conversion methods in both controllers
   - DTO-to-Entity conversion for creation and updates
   - Proper handling of nested objects (roles conversion to string set)
   - Safe exposure of user data without sensitive information

### 🔧 Technical Implementation:
- **HTTP Methods**: Full REST compliance with GET, POST, PUT, PATCH, DELETE
- **Status Codes**: Proper HTTP status codes (201 Created, 404 Not Found, 409 Conflict, etc.)
- **Validation**: Bean validation on DTOs with custom error messages
- **Pagination**: Spring Data pagination support with configurable page size and sorting
- **Search Operations**: Multiple search strategies for both entities and documents
- **Authorization**: Method-level security with Spring Security expressions
- **Logging**: Comprehensive debug and info logging throughout controllers

### 📁 Files Created:
```
src/main/java/com/template/dto/
├── UserDTO.java                    (User entity DTO)
├── UserProfileDTO.java             (UserProfile document DTO)
└── CreateUserRequestDTO.java       (User creation request DTO)

src/main/java/com/template/controller/
├── UserController.java             (User REST API)
└── UserProfileController.java      (UserProfile REST API)
```

### 🎯 API Endpoints Summary:

#### User API (`/api/users`)
- `POST /api/users` - Create user (Admin only)
- `GET /api/users/{id}` - Get user by ID (Authenticated)
- `GET /api/users/username/{username}` - Get user by username (Authenticated)
- `GET /api/users/email/{email}` - Get user by email (Authenticated)
- `GET /api/users` - Get all users with pagination (Admin only)
- `GET /api/users/active` - Get active users (Authenticated)
- `GET /api/users/search/username` - Search by username (Authenticated)
- `GET /api/users/search/name` - Search by name (Authenticated)
- `PUT /api/users/{id}` - Update user (Admin or own profile)
- `PATCH /api/users/{id}/enable` - Enable user (Admin only)
- `PATCH /api/users/{id}/disable` - Disable user (Admin only)
- `DELETE /api/users/{id}` - Delete user (Admin only)
- `GET /api/users/count` - Get user statistics (Admin only)
- `GET /api/users/exists/username/{username}` - Check username exists (Public)
- `GET /api/users/exists/email/{email}` - Check email exists (Public)

#### User Profile API (`/api/user-profiles`)
- `POST /api/user-profiles` - Create profile (Authenticated)
- `GET /api/user-profiles/{id}` - Get profile by ID (Authenticated)
- `GET /api/user-profiles/user/{userId}` - Get profile by user ID (Authenticated)
- `GET /api/user-profiles` - Get all profiles with pagination (Admin only)
- `GET /api/user-profiles/public` - Get public profiles (Public)
- `GET /api/user-profiles/search/location` - Search by location (Authenticated)
- `GET /api/user-profiles/search/bio` - Search by bio keywords (Authenticated)
- `GET /api/user-profiles/search/age-range` - Search by age range (Authenticated)
- `GET /api/user-profiles/search/preference` - Search by preference (Authenticated)
- `GET /api/user-profiles/recent` - Get recently created profiles (Authenticated)
- `GET /api/user-profiles/with-website` - Get profiles with website (Authenticated)
- `PUT /api/user-profiles/{id}` - Update profile (Owner or Admin)
- `DELETE /api/user-profiles/{id}` - Delete profile (Admin only)
- `GET /api/user-profiles/count/location/{location}` - Count by location (Authenticated)

### 🚀 Key Features:
- **Complete CRUD Operations**: Full create, read, update, delete for both User and UserProfile
- **Advanced Search**: Multiple search strategies and filters
- **Security Integration**: Role-based access control with method-level security
- **Data Validation**: Comprehensive input validation with custom error messages
- **Performance**: Pagination support for large datasets
- **API Standards**: RESTful design with proper HTTP methods and status codes
- **Privacy Controls**: Public/private profile separation
- **Analytics**: Count and statistics endpoints for reporting

### ⚙️ Configuration Requirements:
- **Spring Security**: Method-level security must be enabled (`@EnableMethodSecurity`)
- **Validation**: Bean validation must be configured for DTO validation
- **Pagination**: Spring Data pagination support required
- **Jackson**: JSON serialization configured for LocalDateTime handling

**Status:** ✅ Unit 11 Complete - Comprehensive REST API layer implemented with full CRUD operations, advanced search, and proper security integration

---

## Unit 12 - Input Validation and Exception Handling ##
**Date: June 6, 2025**

### ✅ Completed Tasks:
1. **Custom Exception Classes**
   - Created `CustomExceptions.java` with comprehensive exception types:
     - `ResourceNotFoundException` - For entity not found scenarios
     - `ResourceAlreadyExistsException` - For duplicate resource conflicts
     - `InvalidInputException` - For business logic validation failures
     - `UnauthorizedException` - For authentication failures
     - `ForbiddenException` - For authorization failures
     - `ExternalServiceException` - For external service integration failures
     - `DataProcessingException` - For data processing errors
   - All exceptions provide constructors for detailed error messages

2. **Error Response DTO**
   - Created `ErrorResponse.java` as standardized error response format
   - Includes timestamp, status code, error type, message, and request path
   - Supports field-level validation errors with `FieldError` nested class
   - JSON formatting with proper date/time serialization
   - Non-null inclusion to keep responses clean

3. **Global Exception Handler**
   - Created `GlobalExceptionHandler.java` with `@RestControllerAdvice`
   - Comprehensive exception handling for all application scenarios:
     - Custom application exceptions with appropriate HTTP status codes
     - Bean validation errors (`@Valid` annotation support)
     - Spring Security authentication and authorization exceptions
     - HTTP-related exceptions (method not supported, media type, etc.)
     - Database and data integrity violations
     - Generic fallback for unexpected exceptions
   - Consistent error response format across all endpoints
   - Detailed logging for debugging and monitoring

4. **Enhanced DTO Validation**
   - **CreateUserRequestDTO enhancements**:
     - Username pattern validation (alphanumeric, underscore, hyphen only)
     - Strong password requirements (8-128 chars, mixed case, digits, special chars)
     - Name pattern validation (letters, spaces, hyphens, apostrophes only)
   - **UserDTO enhancements**:
     - Username and name pattern validation consistent with creation DTO
     - Improved field validation for consistent data quality
   - **UserProfileDTO enhancements**:
     - Website URL pattern validation with proper URL format
     - Phone number international format validation
     - Date of birth past validation
     - Gender enumeration validation (male, female, other, prefer_not_to_say)
     - Avatar and cover image URL validation for image file formats

### 🔧 Technical Implementation:
- **Exception Hierarchy**: Comprehensive custom exception types for different error scenarios
- **HTTP Status Mapping**: Proper HTTP status codes for each exception type
- **Field Validation**: Bean validation with detailed field-level error messages
- **Security Integration**: Proper handling of Spring Security exceptions
- **Database Integration**: Data access and integrity violation handling
- **Logging Strategy**: Appropriate logging levels (warn for client errors, error for server issues)
- **Pattern Validation**: Regular expressions for data format validation
- **Null Safety**: Proper null checking and safe navigation

### 📁 Files Created:
```
src/main/java/com/template/exception/
├── CustomExceptions.java           (Custom exception types)
└── GlobalExceptionHandler.java     (Global exception handler)

src/main/java/com/template/dto/
└── ErrorResponse.java              (Standardized error response)
```

### 📁 Files Enhanced:
```
src/main/java/com/template/dto/
├── CreateUserRequestDTO.java       (Enhanced validation)
├── UserDTO.java                    (Enhanced validation)
└── UserProfileDTO.java             (Enhanced validation)
```

### 🎯 Exception Handling Coverage:
- **400 Bad Request**: Validation errors, illegal arguments, malformed requests
- **401 Unauthorized**: Authentication failures, invalid credentials
- **403 Forbidden**: Authorization failures, access denied
- **404 Not Found**: Resource not found, invalid endpoints
- **405 Method Not Allowed**: HTTP method not supported
- **409 Conflict**: Resource already exists, data integrity violations
- **415 Unsupported Media Type**: Invalid content types
- **422 Unprocessable Entity**: Data processing errors
- **500 Internal Server Error**: Unexpected application errors
- **503 Service Unavailable**: External service failures

### 🔍 Validation Features:
- **Username Validation**: Alphanumeric with underscore/hyphen support
- **Password Strength**: Complex password requirements with pattern matching
- **Email Validation**: Standard email format validation
- **Name Validation**: International name support with special characters
- **URL Validation**: Website and image URL format validation
- **Phone Validation**: International phone number format
- **Date Validation**: Past date validation for birth dates
- **Enumeration Validation**: Controlled values for gender and similar fields

### ⚙️ Configuration:
- **Global Handler**: `@RestControllerAdvice` provides application-wide exception handling
- **Validation Integration**: Full integration with Spring Boot validation framework
- **Security Integration**: Seamless handling of Spring Security exceptions
- **Logging Integration**: SLF4J logging with appropriate levels for monitoring
- **JSON Serialization**: Consistent error response format with Jackson

### 🚀 Benefits:
- **Consistent Error Responses**: Standardized error format across all endpoints
- **User-Friendly Messages**: Clear, actionable error messages for API consumers
- **Security**: Proper handling of authentication and authorization failures
- **Debugging**: Comprehensive logging for issue diagnosis
- **Validation**: Robust input validation with detailed field-level errors
- **Maintainability**: Centralized exception handling for easy maintenance

**Status:** ✅ Unit 12 Complete - Comprehensive input validation and exception handling implemented successfully

---

## Unit 13 - Database Migrations with Flyway ##
**Date: June 6, 2025**

### ✅ Completed Tasks:
1. **Flyway Integration**
   - Added Flyway dependency to pom.xml
   - Configured Flyway in application.yml with proper settings
   - Added profile-specific Flyway configurations in application-dev.yml and application-prod.yml
   - Created migration directory structure at `src/main/resources/db/migration`
   - Disabled Hibernate auto DDL generation (switched to validate mode)

2. **Version-Controlled Migration Scripts**
   - Created `V1__Create_users_table.sql` for users table schema with proper indexes
   - Created `V2__Create_permissions_table.sql` for permissions with default permissions data
   - Created `V3__Create_roles_table.sql` for roles with default roles data
   - Created `V4__Create_join_tables.sql` for many-to-many relationships with default associations
   - Added timestamps and proper constraints to all tables
   - Implemented consistent naming conventions for primary keys, foreign keys, and indexes

3. **Repeatable Migrations**
   - Created `R__Audit_Triggers.sql` for database triggers that maintain timestamps
   - Implemented automatic timestamp updating via PostgreSQL triggers
   - Made script idempotent (can be run multiple times safely)

4. **Data Initialization**
   - Added default admin user with BCrypt-hashed password
   - Added default roles (ADMIN, USER, GUEST)
   - Added default permissions for common operations
   - Established proper role-permission associations
   - Assigned admin role to default admin user

### 🔧 Technical Implementation:
- **Schema Version Control**: Complete database schema now in version control
- **Migration Strategy**: Baseline on migrate enabled for existing databases
- **Clean Strategy**: Clean disabled in production for safety
- **Validation**: Schema validation on application startup
- **Transaction Management**: Each migration runs in a transaction
- **Error Handling**: Proper error handling with migration validation
- **Security**: Default admin user with secure password hash
- **Backward Compatibility**: Non-destructive migrations for easy rollback

### 📁 Files Created:
```
src/main/resources/db/migration/
├── V1__Create_users_table.sql
├── V2__Create_permissions_table.sql
├── V3__Create_roles_table.sql
├── V4__Create_join_tables.sql
└── R__Audit_Triggers.sql
```

### 📁 Files Modified:
```
pom.xml (added Flyway dependency)
src/main/resources/application.yml (added Flyway configuration)
src/main/resources/application-dev.yml (updated JPA and Flyway settings)
src/main/resources/application-prod.yml (updated JPA and Flyway settings)
```

### 🚀 Key Features:
- **Complete Schema Definition**: All database objects fully defined
- **Data Initialization**: Default users, roles, and permissions
- **Repeatable Migrations**: Automatically updated timestamps and audit data
- **Security Integration**: Proper foreign key constraints and indexes
- **Versioning**: Clear version numbering for migrations
- **Profile Support**: Environment-specific Flyway configurations

### 📊 Entity Relationships in Migrations:
```
User (1) ←→ (N) UserRole (N) ←→ (1) Role
Role (1) ←→ (N) RolePermission (N) ←→ (1) Permission
```

### ⚙️ Configuration:
- **Production**: Clean operations disabled, baseline enabled
- **Development**: Clean operations allowed for testing
- **Locations**: Standard classpath:db/migration location
- **Table**: Standard flyway_schema_history table for tracking

**Status:** ✅ Unit 13 Complete - Database migrations with Flyway successfully implemented

---

## Unit 14 - Comprehensive Logging and Monitoring ##
**Date: June 6, 2025**

### ✅ Completed Tasks:
1. **Structured Logging Configuration**
   - Created `logback-spring.xml` for advanced log formatting, file rotation, and async logging
   - Configured log pattern to include correlation IDs and request IDs for traceability
   - Profile-specific log levels and output destinations (console, file)

2. **Request and Correlation ID Logging**
   - Implemented `RequestLoggingFilter` to inject and propagate correlation/request IDs via MDC
   - All incoming requests and responses now include correlation headers for distributed tracing
   - MDC context propagation for async tasks via `MdcTaskDecorator` and `AsyncConfig`

3. **Logging Configuration Class**
   - Added `LoggingConfig.java` to configure request logging and application startup info
   - Enhanced request logging for debugging and observability

4. **Health and Monitoring Endpoints**
   - Enhanced `HealthController` with detailed health and system info endpoints
   - Integrated with Spring Boot Actuator for health, info, and metrics endpoints
   - Profile-based actuator exposure and security

5. **Configuration Updates**
   - Updated `application.yml` for improved management and logging configuration
   - Ensured log file output and actuator endpoints are profile-aware

### 📁 New/Modified Files:
```
src/main/resources/logback-spring.xml
src/main/java/com/template/config/LoggingConfig.java
src/main/java/com/template/filter/RequestLoggingFilter.java
src/main/java/com/template/config/MdcTaskDecorator.java
src/main/java/com/template/config/AsyncConfig.java
src/main/java/com/template/controller/HealthController.java (enhanced)
src/main/resources/application.yml (updated)
```

### 🔧 Technical Details:
- **Correlation IDs**: All logs and requests traceable via unique IDs
- **Async Logging**: MDC context preserved across async boundaries
- **Actuator Integration**: Health, info, and metrics endpoints available and secured
- **Profile Support**: Logging and monitoring adapt to dev/prod/test profiles
- **Log Rotation**: File logs are rotated and archived for production readiness

**Status:** ✅ Unit 14 Complete - Comprehensive logging and monitoring implemented and verified

---