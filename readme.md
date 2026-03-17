# Auto Gallery Platform

---

## Table of contents
- [Project Overview](#project-overview)  
- [Tech Stack](#tech-stack)  
- [Prerequisites](#prerequisites)  
- [Quick start (local)](#quick-start-local)  
- [Configuration](#configuration)  
- [Database & Migrations](#database--migrations)  
- [Running tests](#running-tests)  
- [How the app is organized](#how-the-app-is-organized)  
- [Selected HTTP endpoints](#selected-http-endpoints)  
- [Reviews microservice (Car Reviews)](#reviews-microservice-car-reviews)  
- [Integration with Feign (main app)](#integration-with-feign-main-app)  

---

## Project overview
Auto Gallery Platform is a Spring Boot web application that lets users register, create and browse car listings, add images, bookmark listings, and read/add/delete reviews. The reviews are provided by a separate microservice (Car Reviews). The web UI uses Thymeleaf templates and a single shared CSS (`modern.css`) with header/footer fragments.

---

## Tech stack
- Java 17+ (project may target a newer JDK — check `pom.xml`)  
- Spring Boot (Web, Data JPA, Security, Validation, Thymeleaf)  
- Spring Cloud OpenFeign (for Reviews microservice integration)  
- Hibernate / JPA, Spring Data repositories  
- H2 (tests), MySQL/Postgres for development/production (configurable)  
- Maven (build tool)  
- JUnit 5 + Mockito + Spring Boot Test for tests

---

## Prerequisites
- Git  
- JDK 17+ installed and `JAVA_HOME` set  
- Maven 3.6+ (or the wrapper `./mvnw`)  
- (Optional) Docker & Docker Compose for local DB/microservice

---

## Quick start (local)

1. Clone repository:
```bash
git clone git@github.com:your-org/auto-gallery-platform.git
cd auto-gallery-platform
```

2. Configure environment variables or `src/main/resources/application.properties` (see next section).

3. Run the application:
```bash
# run with embedded maven wrapper
./mvnw spring-boot:run
# or build and run jar
./mvnw clean package
java -jar target/auto-gallery-platform-*.jar
```

4. Open `http://localhost:8080`.

---

## Configuration

Example `application.properties` (development):
```properties
server.port=8080
spring.datasource.url=jdbc:mysql://localhost:3306/auto_gallery?useSSL=false&serverTimezone=UTC
spring.datasource.username=dbuser
spring.datasource.password=secret
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.show-sql=false

# Feign client base URL for reviews microservice
reviews.service.url=http://localhost:8081
```

Test profile `src/test/resources/application-test.properties`:
```properties
spring.profiles.active=test
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MYSQL
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.security.user.name=test
spring.security.user.password=test
```

Notes:
- For production use `spring.jpa.hibernate.ddl-auto=validate` and a migration tool (Flyway/Liquibase).
- Use environment variables for secrets (DB credentials).

---

## Database & Migrations

Recommended approach for schema changes in production:
1. Add nullable column in migration (deploy).
2. Backfill existing rows (migration or script).
3. Add `NOT NULL` constraint in a subsequent migration only after backfill.

Common pitfalls & fixes:
- `Incorrect datetime value '0000-00-00 00:00:00'` — existing rows have default incompatible values. Make the new column nullable or backfill valid datetimes before `NOT NULL`.
- Unique index collisions with empty strings: prefer storing `NULL` for missing values (DB-level unique constraints treat `NULL` differently than empty-string in many DBs).

If using Flyway, put SQL files in `src/main/resources/db/migration`.

---

## Running tests

Run all tests:
```bash
./mvnw test
```

Guidelines:
- Use `@WebMvcTest` for controller unit tests; mock service dependencies with `@MockBean` / `@MockitoBean`.
- Use `@SpringBootTest` with `@ActiveProfiles("test")` and H2 for integration tests.
- Avoid unnecessary Mockito stubbing (Mockito strictness fails tests with unused stubs). Use `lenient()` if needed.

---

## How the app is organized
```
src/main/java
└─ app
   ├─ model        # JPA entities: User, Listing, Car, Image
   ├─ repository   # Spring Data repositories
   ├─ service      # Business logic
   ├─ web          # Controllers
   ├─ review       # Feign client + DTOs for reviews microservice (main app)
   ├─ security     # Security config and AuthenticationMetadata
   └─ web/dto      # DTOs and form-binding objects
src/main/resources
└─ templates      # Thymeleaf templates (fragments/header, index, home, listing-details, profile, errors)
└─ static         # CSS (modern.css) and images
```

Key patterns:
- Soft delete with `Listing.deleted` boolean; queries use `deleted = false`.
- `AuthenticationMetadata` implements `UserDetails`; used with `@AuthenticationPrincipal`.
- Reusable Thymeleaf header fragment `fragments/header :: header-fragment`.

---

## Selected HTTP endpoints

**Public**
- `GET /` — index
- `GET /login`, `POST /login`
- `GET /register`, `POST /register`

**Authenticated**
- `GET /home` — authenticated list page
- `GET /listings/{id}` — listing details
- `GET /listings/add`, `POST /listings/add` — create listing
- `DELETE /listings/{id}` — soft-delete listing (owner or admin)

**Admin**
- `GET /admin/users` — list non-admin users
- `POST /admin/users/{userId}/makeAdmin` — promote user

**Reviews microservice**
- `GET /api/v1/reviews/listing/{listingId}`
- `POST /api/v1/reviews`
- `DELETE /api/v1/reviews/{reviewId}?userId={...}&isAdmin={true|false}`

---

## Reviews microservice (Car Reviews)
Requirements:
- Own Spring Boot project with separate DB.
- Endpoints:
  - `GET /api/v1/reviews/listing/{listingId}` — returns list of reviews for listing.
  - `POST /api/v1/reviews` — create review (body: `{ listingId, userId, content }`).
  - `DELETE /api/v1/reviews/{reviewId}` — query params `userId`, `isAdmin` for permission.
- Main app calls microservice via Feign client and enriches returned DTOs by looking up usernames from main DB.

Deployment options:
- Same repo as submodule (`/reviews-service`), or separate repo. If same repo, keep independent `pom.xml` per service.

---

## Integration with Feign (main app)
Sample Feign client interface:
```java
@FeignClient(name = "reviewsClient", url = "${reviews.service.url}")
public interface ReviewClient {
    @GetMapping("/api/v1/reviews/listing/{listingId}")
    List<ReviewDto> getReviews(@PathVariable UUID listingId);

    @PostMapping("/api/v1/reviews")
    ReviewDto createReview(CreateReviewRequestDto request);

    @DeleteMapping("/api/v1/reviews/{reviewId}")
    void deleteReview(@PathVariable UUID reviewId, @RequestParam UUID userId, @RequestParam boolean isAdmin);
}
```

Important:
- Keep DTOs synced between services.
- When microservice returns `userId` only, the main app should call `userService.getById(userId)` to set `username` in DTO for display.
