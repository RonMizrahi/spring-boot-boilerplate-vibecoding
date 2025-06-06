# Spring Boot Microservice Template

A comprehensive, production-ready Java microservice boilerplate built with Spring Boot 3.x, featuring best practices for security, persistence, messaging, testing, and deployment.

---

## 🚀 Features
- **Spring Boot 3.x** foundation
- **Security**: Spring Security with JWT authentication & role-based access
- **Persistence**: PostgreSQL (JPA) & MongoDB (Spring Data)
- **Caching**: Redis integration
- **Messaging**: Apache Kafka (producer/consumer)
- **API Documentation**: SpringDoc OpenAPI 3 (Swagger UI)
- **Database Migrations**: Flyway
- **Environment Profiles**: dev, test, prod
- **Comprehensive Logging**: Structured logs, correlation IDs
- **Exception Handling**: Global error responses
- **Testing**: JUnit 5, Testcontainers, integration & unit tests
- **Containerization**: Docker & Docker Compose
- **CI/CD**: GitHub Actions pipeline

---

## 🏗️ Project Structure
```
├── src/main/java/com/template/
│   ├── Application.java                # Main Spring Boot entrypoint
│   ├── config/                        # Configuration classes (DB, Security, Kafka, etc.)
│   ├── controller/                    # REST API controllers
│   ├── document/                      # MongoDB documents
│   ├── dto/                           # Data Transfer Objects
│   ├── entity/                        # JPA entities (PostgreSQL)
│   ├── exception/                     # Global exception handling
│   ├── repository/                    # JPA & MongoDB repositories
│   ├── security/                      # JWT, security filters, RBAC
│   └── service/                       # Business logic
│
├── src/main/resources/
│   ├── application.yml                # Main config
│   ├── application-dev.yml            # Dev profile config
│   ├── application-prod.yml           # Prod profile config
│   ├── db/migration/                  # Flyway migration scripts
│   └── logback-spring.xml             # Logging config
│
├── src/test/java/com/template/        # Unit & integration tests
│
├── Dockerfile                         # Container build
├── docker-compose.yml                 # Multi-service orchestration
├── pom.xml                            # Maven build file
└── ...
```

---

## ⚡ Quick Start
1. **Clone the repo:**
   ```sh
   git clone <your-repo-url>
   cd vibecodingproject
   ```
2. **Configure environment:**
   - Set DB, Redis, Kafka, MongoDB credentials in `application-*.yml` or as environment variables.
3. **Run database migrations:**
   ```sh
   mvn flyway:migrate
   ```
4. **Build & run locally:**
   ```sh
   mvn clean spring-boot:run
   ```
5. **Run tests:**
   ```sh
   mvn test
   ```
6. **API docs:**
   - Visit [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
7. **Run with Docker:**
   ```sh
   docker-compose up --build
   ```

---

## 🧩 Main Modules & Technologies
- **Spring Boot Starters**: Web, Security, Data JPA, Data MongoDB, Data Redis, Kafka, Validation, Actuator
- **PostgreSQL**: Relational DB (JPA)
- **MongoDB**: NoSQL document store
- **Redis**: Caching & session storage
- **Kafka**: Event-driven messaging
- **Flyway**: Database migrations
- **JWT**: Stateless authentication
- **JUnit 5 & Testcontainers**: Testing
- **SpringDoc OpenAPI**: API docs
- **Docker**: Containerization
- **GitHub Actions**: CI/CD

---

## 📚 Implementation Roadmap (from `Plan.md`)
- **MVP Units (1-9):**
  - Project structure, dependencies, profiles
  - PostgreSQL & MongoDB integration
  - Redis caching, Kafka messaging
  - Security & JWT authentication
- **Enhancements (10-16):**
  - Role-based access, full REST API, validation, Flyway, logging, OpenAPI, Docker
- **Polish (17-20):**
  - Unit & integration tests, CI/CD, production configs

See `Plan.md` for full details and progress.

---

## 📝 License
MIT or your preferred license.

---

## 🙏 Credits
- Spring Boot, Spring Security, Spring Data, SpringDoc
- Community best practices & open source inspiration
