# GameHub — Multi-Game Arcade Web Platform

*A complete, full-stack browser-based gaming portal built with Spring Boot, Spring Security (JWT), Spring Data JPA, H2/MySQL, and HTML5 Canvas API.*  
*Prepared for B.Tech Computer Science / Information Technology Minor Project.*

---

## 1. Project Overview

**GameHub** is an arcade gaming platform featuring an integrated ecosystem:
- **One unified identity**: single account, single JWT token, and one profile across all games.
- **4 Playable Canvas Games**: **Snake**, **Flappy Bird**, **2048**, and **Tic Tac Toe** (with Minimax AI).
- **Shared Game Engine**: common requestAnimationFrame loop with delta time, DPI scaling, pause/resume, and HUD.
- **Anti-Cheat Validation**: server-side rate-check verifies that score vs. session duration is physically plausible before recording.
- **Dynamic Leaderboards**: per-game and global rankings with fast reads.
- **Progression & Achievement System**: unlocks badges, tracks playtime stats, and awards XP to level up.
- **Dual-Database Support**: instant zero-setup in-memory **H2 Database** for local development & demonstration; production-ready for **MySQL 8**.

---

## 2. Technology Stack

| Layer | Technology | Purpose |
| --- | --- | --- |
| **Backend** | Java 17+ (Java 25 verified), Spring Boot 3.3.5 | Robust, layered REST API |
| **Security** | Spring Security 6 + JJWT 0.12.6 | Stateless JWT authentication (HMAC-SHA256) |
| **Persistence** | Spring Data JPA (Hibernate) | Relational mapping, query abstraction |
| **Databases** | H2 (in-memory dev) & MySQL 8 (production) | Instant local run without external DB installation |
| **API Docs** | springdoc-openapi (Swagger UI 2.6.0) | Live interactive API documentation |
| **Frontend** | HTML5, CSS3 Custom Properties, Vanilla ES6 JavaScript | Cyberpunk dark arcade UI, zero framework bloat |
| **Game Engine** | HTML5 Canvas API + `GameBase.js` | High-DPI scaling, fixed delta-time loop |
| **Build Tool** | Apache Maven 3.9.6 + Maven Wrapper (`mvnw.cmd`) | Self-contained one-command execution |

---

## 3. How to Run the Project (Windows 11)

### Option A: Using the included Maven Wrapper (Recommended)

Open **Command Prompt (`cmd`)** or **PowerShell** in the project directory (`d:\projects\minor project`):

```cmd
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

### Option B: Using Installed Apache Maven

```cmd
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Option C: Running the Tests

```cmd
.\mvnw.cmd test
```

---

## 4. Key Localhost URLs

Once the application starts, open your browser:

| Resource | URL | Description |
| --- | --- | --- |
| **GameHub Portal** | [http://localhost:8080](http://localhost:8080) | Main Arcade Dashboard |
| **Game Catalog API** | [http://localhost:8080/api/games](http://localhost:8080/api/games) | Active games list JSON |
| **API Health Check** | [http://localhost:8080/api/test](http://localhost:8080/api/test) | Service health check |
| **H2 Database Console** | [http://localhost:8080/h2-console](http://localhost:8080/h2-console) | Web SQL console (JDBC URL: `jdbc:h2:mem:gamehub`, Username: `sa`, Password: *blank*) |
| **Swagger UI** | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) | Interactive OpenAPI documentation |
| **Global Leaderboard** | [http://localhost:8080/leaderboard.html](http://localhost:8080/leaderboard.html) | Rankings table |
| **Player Profile** | [http://localhost:8080/profile.html](http://localhost:8080/profile.html) | XP, Level, Badges |

### Default Seed Demo Accounts

| Username | Password | Role | Description |
| --- | --- | --- | --- |
| `player` | `password123` | PLAYER | Pre-seeded player with XP and history |
| `admin` | `adminpassword` | ADMIN | Pre-seeded platform admin |
| *Register new* | *Any 8+ char password* | PLAYER | Instant account creation |

---

## 5. Playable Games Included

1. **Snake** (`/games/snake.html`)
   - Grid-based neon snake with directional look.
   - Normal apples (+10 pts) and bonus golden apples (+30 pts) with countdown timer.
   - Speed progression and boundary collision.
2. **Flappy Bird** (`/games/flappy.html`)
   - Realistic gravity, velocity-dependent pitch, and flap impulse.
   - Moving neon pipes with collision detection.
   - Space or click anywhere on screen to flap.
3. **2048** (`/games/2048.html`)
   - 4x4 sliding tile puzzle with merge animations and color gradation.
   - Keyboard arrows / WASD to slide.
   - Automatic game-over detection when no moves remain.
4. **Tic Tac Toe** (`/games/tictactoe.html`)
   - Interactive 3x3 canvas duel: Player (X - Cyan) vs AI (O - Purple).
   - Minimax decision-making algorithm: blocks player, takes center, and seeks win lines.

---

## 6. Architecture & Design Principles

```
Browser (Canvas + ES Modules)
          │ HTTP / JSON (Bearer JWT)
          ▼
Spring Boot Controllers (AuthController, GameController, ScoreController, LeaderboardController)
          │ DTOs (Request / Response)
          ▼
Service Layer (AuthService, ScoreService, LeaderboardService, AchievementService)
          │ Entities & Transactions
          ▼
Spring Data JPA Repositories (UserRepository, ScoreRepository, HighScoreRepository)
          │ SQL Queries & Indexes
          ▼
Database (H2 In-Memory for Dev / MySQL 8 for Prod)
```

1. **Strict Layered Separation**: Controllers never interact directly with database repositories. Services contain all business rules and transaction boundaries (`@Transactional`).
2. **DTOs at API Boundary**: Internal database entity objects are never returned directly to HTTP responses. This eliminates lazy-loading serialization bugs and safeguards sensitive fields (like password hashes).
3. **Anti-Cheat Plausibility Bound**: Every game defines a `max_score_per_second`. During score submission, `ScoreValidator` checks:
   $$\text{Rate} = \frac{\text{Score}}{\text{Duration (seconds)}} \le \text{Max Score Per Second}$$
   Any submission exceeding this threshold is rejected with HTTP 422 Unprocessable Entity.

---

## 7. College Evaluation & Viva Voce Q&A

### Q1: Why did you use JWT instead of standard HTTP sessions?
**Answer**: REST architectural principles require statelessness. Server-side session storage (like `HttpSession`) binds user state to a single server instance, necessitating sticky sessions or external caches (e.g., Redis) when scaling horizontally. With JWT, the token is digitally signed using HMAC-SHA256 and stored on the client (`localStorage`). Each request sends the token in the `Authorization: Bearer <token>` header, allowing any server instance to verify authenticity independently without extra database lookups.

### Q2: Why BCrypt for password hashing?
**Answer**: Standard cryptographic hashes like SHA-256 or MD5 are designed for high throughput, making them vulnerable to brute-force and rainbow table attacks on modern GPUs. BCrypt incorporates a randomized salt to prevent rainbow table attacks and uses an adaptable work factor (cost) to intentionally slow down computation, making offline dictionary attacks computationally infeasible.

### Q3: How does the client-side game engine handle different monitor refresh rates?
**Answer**: Many naive Canvas games tie movement directly to individual frames (`requestAnimationFrame`). On a 144Hz monitor, such games would run more than twice as fast as on a 60Hz screen. Our `GameBase.js` calculates delta time:
$$dt = \frac{\text{currentTime} - \text{lastTime}}{1000}$$
All physical movements, gravity, and speeds are multiplied by `dt` (in seconds), guaranteeing consistent gameplay speed across all devices and refresh rates.

### Q4: Why maintain both `scores` and `high_scores` tables?
**Answer**: The `scores` table records full session history (timestamp, duration, score) used for user activity logs, daily streaks, and weekly leaderboards. The `high_scores` table maintains a denormalized unique record per `(user_id, game_id)`. This allows all-time leaderboard queries to execute via an indexed lookup rather than performing an expensive `GROUP BY` and `MAX(score)` over millions of historical rows.
# gamehub 
