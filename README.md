# Game2048

Project Overview
----------------
A concise, production-ready Java web implementation of the 2048 game that provides an interactive UI and REST APIs for starting games, making moves, persisting scores, and retrieving game history — useful for demonstrating real-time game logic, backend design, and web integration in interviews.

Tech Stack
----------
| Component             | Detected / Placeholder |
|----------------------:|:-----------------------|
| Java                 | <Java version — e.g. 11 or 17> (see pom.xml / build.gradle) |
| Framework            | Spring Boot (<spring-boot-starter-web>, <spring-boot-starter-data-jpa>, etc.) |
| Persistence          | <Database — e.g. H2 / PostgreSQL / MySQL> (see application.properties) |
| Security             | <Spring Security / JWT?> (detect by dependency: spring-boot-starter-security or jjwt) |
| Caching              | <Redis?> (detect by dependency: spring-boot-starter-data-redis) |
| Build Tool           | <Maven or Gradle> (pom.xml or build.gradle) |
| Frontend / UI        | Java + CSS, served from static resources (src/main/resources/static) |
| API Documentation    | <Swagger/OpenAPI present?> (detect by dependency: springdoc-openapi or springfox) |
| Testing              | JUnit 5, Mockito (detect by test dependencies) |
| Packaging            | Spring Boot executable jar |

Key Features (with technical details)
------------------------------------
- Game Engine: Server-side implementation of the 2048 game logic (tile merges, score accumulation, random tile generation). Game state model is persisted so games can be resumed.
- RESTful API: Controllers expose endpoints to create a new game, apply moves (up/down/left/right), retrieve game state, and list historical games/scores.
- Persistence: Uses Spring Data JPA repositories to map Game and Score entities to the database for durability and analytics.
- Transactional Integrity: Service-layer transaction boundaries ensure move operations are atomic and consistent (e.g., @Transactional on Service methods handling a move + persistence).
- Concurrency Control: Game state updates are handled in the Service layer—optimistic/pessimistic locking patterns may be used to avoid race conditions when multiple requests try to mutate the same game.
- Caching (optional): If Redis is integrated, frequently read endpoints (leaderboard, top scores) are cached for performance.
- Security (optional): If Spring Security/JWT is present, token-based auth secures private APIs (create game, submit score) while allowing public read endpoints (leaderboard).
- Frontend: Minimal CSS-driven UI served by Spring Boot static resources to play the game in a browser (progressive enhancement, responsive layout).

System Architecture
-------------------
- Controller (REST) layer
  - Receives HTTP requests, validates input DTOs, maps to Service calls, returns DTOs/HTTP status.
- Service (Business) layer
  - Encapsulates game rules, coordinates repository operations, handles transactions and concurrency.
- Repository (Data) layer
  - Spring Data JPA repositories that persist Game, Tile, Score, Player entities.
- Domain / Model
  - Entities: Game, Tile (or board stored as 2D array/JSON), Player/Score, Audit fields.
- Web / Static
  - Static assets (HTML/CSS/JS) served from src/main/resources/static for browser play.
- Optional components: Security filters (JWT), Redis cache, OpenAPI filter for docs.

API Documentation
-----------------
Note: I cannot detect if OpenAPI/Swagger is integrated without the repo. If present, you will typically see springdoc-openapi-ui or springfox dependencies and an /swagger-ui.html or /swagger-ui/index.html endpoint.

Example core endpoints (replace with actual paths from your Controllers):
- POST /api/games
  - Start a new game. Request: playerId (optional), boardSize (optional). Response: gameId, initial board, currentScore.
- POST /api/games/{gameId}/moves
  - Apply a move. Request: { direction: "UP" | "DOWN" | "LEFT" | "RIGHT" }. Response: updated board, delta score, game status (IN_PROGRESS / WON / LOST).
- GET /api/games/{gameId}
  - Retrieve current game state and history for a gameId.

(When you provide source files I will extract the real endpoints, request/response DTOs and model example JSONs.)

Database Schema (core entities)
------------------------------
Based on typical 2048 implementations, core entities likely include:

- Game
  - id (PK)
  - playerId (FK, optional)
  - board (persisted as JSON or normalized Tile rows)
  - score (current)
  - status (IN_PROGRESS, WON, LOST)
  - created_at, updated_at

- Tile (optional normalized model)
  - id (PK)
  - game_id (FK)
  - row, col, value

- Player / Score
  - id
  - name / username
  - best_score
  - created_at

Entity relationships:
- Player 1 — * Game
- Game 1 — * Tile (if tiles are normalized)
- Player 1 — * Score (or score aggregated on Player)

Getting Started
---------------
Prerequisites
- Java 11+ (LTS recommended) — check pom.xml or build.gradle for target.
- Maven 3.6+ or Gradle 6+ depending on the build tool in repo.
- Database: if application uses H2 you can run in-memory; for PostgreSQL/MySQL ensure DB is running and configured in application.properties.
- Optional: Redis for caching (if present)

Build & Run (Maven)
- mvn clean package
- java -jar target/<artifact>-<version>.jar
- Or for dev: mvn spring-boot:run

Build & Run (Gradle)
- ./gradlew clean build
- java -jar build/libs/<artifact>-<version>.jar
- Or for dev: ./gradlew bootRun

Environment variables / application properties
- Typical properties to check:
  - spring.datasource.url / username / password
  - spring.jpa.hibernate.ddl-auto
  - spring.redis.host / port (if Redis)
  - spring.profiles.active (dev / prod)

Development Challenges & Suggested Optimizations
-----------------------------------------------
1) Challenge: Concurrent updates to the same Game instance
   - Problem: Multiple move requests or retries could cause inconsistent board or score.
   - Suggestion: Use database-level optimistic locking (@Version field on Game entity) or synchronize Service methods for single-user games. For distributed setups, use Redis distributed locks (Redisson) for per-game locks.

2) Challenge: Board persistence / storage overhead
   - Problem: Persisting each Tile as rows can be heavy and slower for reads/writes.
   - Suggestion: Serialize the board as compact JSON or bit-encoded representation stored in a TEXT/BLOB column for fast read/write, and only normalize tiles when you need per-tile queries. Use caching for hot game sessions to avoid frequent DB round-trips.

3) Performance / Load
   - If many concurrent users: add caching for leaderboard endpoints, tune DB indices, and use connection pooling. Expose metrics (Micrometer) for bottleneck discovery.

How I will finalize this README
-------------------------------
I can replace placeholders with exact values and produce:
- Concrete Tech Stack table (exact Java, Spring Boot version and modules),
- Exact API endpoints and sample request/response payloads,
- Concrete Database schema (entity fields and relationships),
- Precise startup commands tailored to your build tool and profile.

Please either:
1) Make the repo accessible, or
2) Paste these files (or their content):
   - pom.xml or build.gradle
   - src/main/java/**/Application.java (main Spring Boot class)
   - src/main/java/**/controller/* (Controllers)
   - src/main/java/**/service/* (Service classes)
   - src/main/java/**/repository/* (Repository interfaces)
   - src/main/java/**/model|entity/* (Entities/DTOs)
   - src/main/resources/application*.properties or .yml
   - Any README or docs you currently have

Once I have the code, I will regenerate README.md with precise extracted details for your interview.
