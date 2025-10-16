# Yushan Gamification Service

> 🎮 **Gamification Service for Yushan Webnovel Platform.** - Manages achievements, rewards, leaderboards, and progression systems to create an engaging, game-like reading experience.

# Yushan Platform - Gamification Service Setup Guide

## Architecture Overview

```
┌─────────────────────────────┐
│   Eureka Service Registry   │
│       localhost:8761        │
└──────────────┬──────────────┘
               │
┌──────────────┴──────────────┐
│   Service Registration &     │
│      Discovery Layer         │
└──────────────┬──────────────┘
               │
    ┌──────────┴──────────┬───────────────┬──────────┬──────────┐
    │                     │               │          │          │
    ▼                     ▼               ▼          ▼          ▼
┌────────┐          ┌─────────┐  ┌────────────┐ ┌──────────┐ ┌──────────┐
│  User  │          │ Content │  │ Engagement │ │Gamifica- │ │Analytics │
│Service │◄────────►│ Service │  │  Service   │ │  tion    │ │ Service  │
│ :8081  │          │  :8082  │  │   :8084    │◄┤ Service  │ │  :8083   │
└────────┘          └─────────┘  └────────────┘ │  :8085   │◄└──────────┘
    │                     │              │       └─────┬────┘
    └─────────────────────┴──────────────┴─────────────┘
                    Inter-service Communication
                      (via Feign Clients)
                              │
                    ┌─────────┴─────────┐
                    │   Achievement      │
                    │   Processing &     │
                    │   Reward System    │
                    └───────────────────┘
```

---
## Prerequisites

Before setting up the Gamification Service, ensure you have:
1. **Java 21** installed
2. **Maven 3.8+** or use the included Maven wrapper
3. **Eureka Service Registry** running
4. **PostgreSQL 15+** (for gamification data storage)
5. **Redis** (for caching leaderboards and real-time rankings)

---
## Step 1: Start Eureka Service Registry

**IMPORTANT**: The Eureka Service Registry must be running before starting any microservice.

```bash
# Clone the service registry repository
git clone https://github.com/maugus0/yushan-platform-service-registry
cd yushan-platform-service-registry

# Option 1: Run with Docker (Recommended)
docker-compose up -d

# Option 2: Run locally
./mvnw spring-boot:run
```

### Verify Eureka is Running

- Open: http://localhost:8761
- You should see the Eureka dashboard

---

## Step 2: Clone the Gamification Service Repository

```bash
git clone https://github.com/maugus0/yushan-gamification-service.git
cd yushan-gamification-service

# Option 1: Run with Docker (Recommended)
docker-compose up -d

# Option 2: Run locally (requires PostgreSQL 15 and Redis to be running beforehand)
./mvnw spring-boot:run
```

---

## Expected Output

### Console Logs (Success)

```
2024-10-16 10:30:15 - Starting GamificationServiceApplication
2024-10-16 10:30:18 - Tomcat started on port(s): 8085 (http)
2024-10-16 10:30:20 - DiscoveryClient_GAMIFICATION-SERVICE/gamification-service:8085 - registration status: 204
2024-10-16 10:30:20 - Started GamificationServiceApplication in 8.8 seconds
```

### Eureka Dashboard

```
Instances currently registered with Eureka:
✅ GAMIFICATION-SERVICE - 1 instance(s)
   Instance ID: gamification-service:8085
   Status: UP (1)
```

---

## API Endpoints

### Health Check
- **GET** `/api/v1/health` - Service health status

### Achievements
- **GET** `/api/v1/achievements` - List all available achievements
- **GET** `/api/v1/achievements/{achievementId}` - Get achievement details
- **POST** `/api/v1/achievements` - Create new achievement (admin)
- **GET** `/api/v1/achievements/users/{userId}` - Get user's achievements
- **POST** `/api/v1/achievements/users/{userId}/unlock` - Unlock achievement for user

### Badges
- **GET** `/api/v1/badges` - List all available badges
- **GET** `/api/v1/badges/{badgeId}` - Get badge details
- **GET** `/api/v1/badges/users/{userId}` - Get user's badges
- **POST** `/api/v1/badges/users/{userId}/award` - Award badge to user

### Points & Levels
- **GET** `/api/v1/points/users/{userId}` - Get user's points
- **POST** `/api/v1/points/users/{userId}/add` - Add points to user
- **GET** `/api/v1/levels/users/{userId}` - Get user's level
- **GET** `/api/v1/levels/config` - Get level configuration

### Leaderboards
- **GET** `/api/v1/leaderboards/global` - Get global leaderboard
- **GET** `/api/v1/leaderboards/weekly` - Get weekly leaderboard
- **GET** `/api/v1/leaderboards/monthly` - Get monthly leaderboard
- **GET** `/api/v1/leaderboards/users/{userId}/rank` - Get user's rank
- **GET** `/api/v1/leaderboards/genre/{genreId}` - Get genre-specific leaderboard

### Rewards
- **GET** `/api/v1/rewards` - List available rewards
- **GET** `/api/v1/rewards/users/{userId}` - Get user's rewards
- **POST** `/api/v1/rewards/users/{userId}/claim` - Claim reward
- **GET** `/api/v1/rewards/users/{userId}/history` - Get reward history

### Streaks
- **GET** `/api/v1/streaks/users/{userId}` - Get user's streak info
- **POST** `/api/v1/streaks/users/{userId}/check-in` - Daily check-in
- **GET** `/api/v1/streaks/users/{userId}/history` - Get streak history

### Quests/Challenges
- **GET** `/api/v1/quests` - List active quests
- **GET** `/api/v1/quests/{questId}` - Get quest details
- **GET** `/api/v1/quests/users/{userId}` - Get user's active quests
- **POST** `/api/v1/quests/users/{userId}/start` - Start a quest
- **POST** `/api/v1/quests/users/{userId}/complete` - Complete a quest

---

## Key Features

### 🏆 Achievement System
- Milestone-based achievements
- Hidden achievements
- Progressive achievements (Bronze, Silver, Gold)
- Category-based achievements (Reading, Social, Engagement)
- Achievement tracking and notifications

### 🎖️ Badge Collection
- Collectible badges
- Limited edition badges
- Event-specific badges
- Badge display on profiles
- Badge rarity levels

### 📊 Points & Levels
- Experience points (XP) system
- Level progression
- Points for various activities:
  - Reading chapters
  - Writing reviews
  - Daily login
  - Social interactions
  - Completing challenges

### 🥇 Leaderboards
- Global rankings
- Time-based leaderboards (daily/weekly/monthly)
- Genre-specific rankings
- Friend leaderboards
- Real-time rank updates
- Cached for performance

### 🎁 Reward System
- Redeemable rewards
- Milestone rewards
- Daily/Weekly bonuses
- Special event rewards
- Reward marketplace

### 🔥 Streak System
- Daily reading streaks
- Streak bonuses
- Streak freeze mechanics
- Longest streak tracking
- Streak milestones

### 🎯 Quest System
- Daily quests
- Weekly challenges
- Special event quests
- Progressive quest chains
- Quest rewards and XP

---

## Database Schema

The Gamification Service uses the following key entities:

- **Achievement** - Achievement definitions
- **UserAchievement** - User-achievement mappings
- **Badge** - Badge definitions
- **UserBadge** - User-badge mappings
- **UserPoints** - User points and level data
- **LeaderboardEntry** - Leaderboard rankings
- **Reward** - Reward catalog
- **UserReward** - User-reward history
- **Streak** - User streak data
- **Quest** - Quest definitions
- **UserQuest** - User quest progress

---

## Next Steps

Once this basic setup is working:
1. ✅ Create database entities (Achievement, Badge, UserPoints, etc.)
2. ✅ Set up Flyway migrations
3. ✅ Create repositories and services
4. ✅ Implement achievement unlock logic
5. ✅ Set up leaderboard caching with Redis
6. ✅ Add Feign clients for inter-service communication
7. ✅ Implement event listeners for automatic achievement triggers
8. ✅ Set up scheduled jobs for leaderboard updates
9. ✅ Add notification system for unlocked achievements
10. ✅ Implement anti-cheat mechanisms

---

## Troubleshooting

**Problem: Service won't register with Eureka**
- Ensure Eureka is running: `docker ps`
- Check logs: Look for "DiscoveryClient" messages
- Verify defaultZone URL is correct

**Problem: Port 8085 already in use**
- Find process: `lsof -i :8085` (Mac/Linux) or `netstat -ano | findstr :8085` (Windows)
- Kill process or change port in application.yml

**Problem: Database connection fails**
- Verify PostgreSQL is running: `docker ps | grep yushan-postgres`
- Check database credentials in application.yml
- Test connection: `psql -h localhost -U yushan_gamification -d yushan_gamification`

**Problem: Redis connection fails**
- Verify Redis is running: `docker ps | grep redis`
- Check Redis connection: `redis-cli ping`
- Verify Redis host and port in application.yml

**Problem: Build fails**
- Ensure Java 21 is installed: `java -version`
- Check Maven: `./mvnw -version`
- Clean and rebuild: `./mvnw clean install -U`

**Problem: Leaderboard not updating**
- Check scheduled job logs
- Verify Redis cache is working
- Check if background tasks are enabled
- Review leaderboard update interval configuration

**Problem: Achievements not unlocking**
- Check event listener logs
- Verify Feign client connections to other services
- Review achievement criteria logic
- Check database triggers and constraints

---

## Performance Tips
1. **Leaderboard Caching**: Use Redis for frequently accessed leaderboards
2. **Batch Processing**: Process achievement checks in batches
3. **Async Operations**: Use async processing for non-critical updates
4. **Database Indexing**: Index user_id, timestamp, and ranking columns
5. **Rate Limiting**: Implement rate limits on points addition endpoints

---

## Event System
The Gamification Service listens to events from other services:
- **Reading Events**: From Engagement Service (chapters read, time spent)
- **Social Events**: From User Service (follows, reviews, comments)
- **Content Events**: From Content Service (novel ratings)

These events trigger automatic achievement unlocks and point awards.

---

## Inter-Service Communication
The Gamification Service communicates with:
- **User Service**: Fetch user profile data
- **Engagement Service**: Track reading activity
- **Analytics Service**: Send gamification metrics
- **Content Service**: Verify content-related achievements

---

## Security Considerations
- Validate all point additions to prevent cheating
- Implement rate limiting on point-earning endpoints
- Use cryptographic verification for achievement unlocks
- Audit log all reward claims
- Implement cooldown periods for repeatable actions
- Monitor for suspicious activity patterns

---

## Monitoring
The Gamification Service exposes metrics through:
- Spring Boot Actuator endpoints (`/actuator/metrics`)
- Custom gamification metrics (achievements unlocked, points awarded)
- Leaderboard refresh status
- Redis cache hit rates

---

## Anti-Cheat Measures
1. **Rate Limiting**: Prevent rapid repeated actions
2. **Activity Validation**: Verify actions with source services
3. **Pattern Detection**: Monitor for suspicious patterns
4. **Manual Review**: Flag unusual point accumulation
5. **Rollback Capability**: Ability to revert fraudulent gains

---

## License
This project is part of the Yushan Platform ecosystem.
