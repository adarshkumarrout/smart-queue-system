# ⚡ Smart Queue Management System

> A production-ready, event-driven queue management platform for walk-in businesses — built with Java Spring Boot, React, Kafka, Redis, WebSockets, MySQL, and MongoDB.

---

## 🎯 Features

### User Features
- 🔐 JWT-based Register / Login
- 🎫 Join queues remotely with priority selection (Normal / VIP / Emergency)
- 📍 Real-time queue position & estimated wait time
- 🔔 WebSocket push notifications when turn is near
- 📜 Token history dashboard

### Admin Features
- 🏢 Multi-tenant: manage multiple businesses & branches
- 📋 Create and manage queues per branch
- 👥 Add staff and assign to queues
- ▶ Serve next token with one click
- 🚫 Mark tokens as no-show
- 📊 Live analytics dashboard (served, no-shows, wait times)
- 🔄 Real-time auto-refresh via WebSockets

### System Features
- ⚡ Kafka event streaming (queue.join / queue.served / queue.no_show)
- 🔴 Redis caching for positions, wait times, queue sizes
- 🤖 Auto no-show detection via scheduled jobs
- 🚧 Rate limiting (max queue joins per hour)
- 🏗 SOLID principles, DTO pattern, global exception handling

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.2 |
| Security | Spring Security + JWT |
| Real-time | WebSockets (STOMP over SockJS) |
| Event Streaming | Apache Kafka |
| Cache | Redis (Lettuce) |
| Main DB | MySQL 8 |
| Log/Event DB | MongoDB 6 |
| Frontend | React 18, Axios, SockJS+STOMP |
| DevOps | Docker + Docker Compose |
| API Docs | SpringDoc OpenAPI (Swagger UI) |

---

## 🏗 Architecture Overview (HLD)

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                             │
│   React SPA (Browser)   ←→   WebSocket (SockJS/STOMP)          │
└──────────────────────────────┬──────────────────────────────────┘
                               │ HTTP / WS
┌──────────────────────────────▼──────────────────────────────────┐
│                   SPRING BOOT API SERVER                        │
│  ┌────────────┐  ┌──────────────┐  ┌─────────────────────────┐ │
│  │ REST       │  │ WebSocket    │  │ Spring Security (JWT)   │ │
│  │ Controllers│  │ STOMP Broker │  │ + Rate Limiting         │ │
│  └────────────┘  └──────────────┘  └─────────────────────────┘ │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │              SERVICE LAYER (Business Logic)                │ │
│  │  QueueService · TokenService · PredictionService          │ │
│  │  StaffService · AnalyticsService · NoShowDetection        │ │
│  └────────────────────────────────────────────────────────────┘ │
└──────┬────────────────┬──────────────┬──────────────┬───────────┘
       │                │              │              │
  ┌────▼────┐    ┌──────▼──────┐  ┌───▼───┐   ┌─────▼─────┐
  │  MySQL  │    │   MongoDB   │  │ Redis │   │  Kafka    │
  │ (main)  │    │(events/logs)│  │(cache)│   │(streaming)│
  └─────────┘    └─────────────┘  └───────┘   └─────┬─────┘
                                                      │
                                            ┌─────────▼──────────┐
                                            │  Kafka Consumers   │
                                            │ → Update Redis     │
                                            │ → Save to MongoDB  │
                                            │ → Push WS updates  │
                                            └────────────────────┘
```

### Kafka Event Flow
```
User Joins Queue
  → TokenService.joinQueue()
  → KafkaProducer.publishJoinEvent() → [queue.join topic]
  → KafkaConsumer.handleJoinEvent()
      → Redis: setQueuePosition, setQueueWaitTime
      → MongoDB: persist QueueEvent
      → WebSocket: broadcast /topic/queue/{id}
```

### Scalability Strategy
- **Stateless services**: No session state; JWT per request
- **Horizontal scaling**: Multiple Spring Boot instances behind load balancer
- **Kafka partitioning**: 3 partitions per topic for parallel consumers
- **Redis**: Shared cache layer, avoids DB hit on every position query
- **MongoDB**: Append-only event log, scales independently

---

## 📁 Project Structure

```
smart-queue-system/
├── docker-compose.yml
├── README.md
├── backend/
│   ├── pom.xml
│   └── src/main/java/com/smartqueue/
│       ├── SmartQueueApplication.java
│       ├── model/            # JPA Entities + MongoDB Documents
│       ├── repository/       # JPA + MongoDB repositories
│       ├── dto/request/      # Request DTOs
│       ├── dto/response/     # Response DTOs
│       ├── service/          # Business logic
│       ├── controller/       # REST controllers
│       ├── security/         # JWT filter + UserDetailsService
│       ├── config/           # Security, Redis, Kafka, WS, Swagger
│       ├── kafka/            # Producer + Consumer + Message
│       ├── cache/            # Redis cache service
│       ├── websocket/        # WS notification service
│       ├── exception/        # Global exception handler
│       └── util/             # PredictionService, TokenGenerator
└── frontend/
    ├── package.json
    ├── public/index.html
    └── src/
        ├── App.js
        ├── context/AuthContext.js
        ├── pages/            # LoginPage, Register, UserDashboard, AdminDashboard, QueuePage
        ├── components/       # Navbar, TokenCard, QueueCard, Notification
        ├── services/         # api.js, queueService.js, adminService.js
        ├── websocket/        # wsClient.js
        └── styles/global.css
```

---

## ⚙️ Local Setup Instructions

### Prerequisites
- Java 17+
- Node.js 18+
- Maven 3.8+
- Docker & Docker Compose

### Step 1 — Start Infrastructure
```bash
cd smart-queue-system
docker-compose up -d
```
Wait ~30 seconds for all services to be healthy.

### Step 2 — Run Backend
```bash
cd backend
mvn spring-boot:run
```
Backend starts at **http://localhost:8080**  
Swagger UI: **http://localhost:8080/swagger-ui.html**

### Step 3 — Run Frontend
```bash
cd frontend
npm install
npm start
```
Frontend starts at **http://localhost:3000**

### Step 4 — Seed Data (optional)
The `docs/init.sql` file is auto-loaded by MySQL container.

To create an admin user, register via the API and manually update the role:
```sql
UPDATE users SET role = 'ADMIN' WHERE username = 'admin';
```

---

## 📡 API Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | /api/auth/register | ❌ | Register user |
| POST | /api/auth/login | ❌ | Login, get JWT |
| GET | /api/queues/branch/{id} | ✅ | Get queues in branch |
| GET | /api/queues/{id} | ✅ | Queue snapshot |
| POST | /api/queues/join | ✅ USER | Join queue |
| POST | /api/queues/{id}/serve-next | ✅ | Serve next token |
| GET | /api/queues/my-tokens | ✅ | My token history |
| POST | /api/admin/businesses | ✅ ADMIN | Create business |
| POST | /api/admin/branches | ✅ ADMIN | Create branch |
| POST | /api/admin/queues | ✅ ADMIN | Create queue |
| POST | /api/admin/staff/{uid}/branch/{bid} | ✅ ADMIN | Add staff |
| POST | /api/admin/staff/{sid}/assign-queue/{qid} | ✅ ADMIN | Assign staff |
| GET | /api/admin/queues/{id}/analytics | ✅ ADMIN | Queue analytics |
| POST | /api/admin/tokens/{id}/no-show | ✅ ADMIN | Mark no-show |

---

## 🔌 WebSocket Testing

Connect with SockJS to `http://localhost:8080/ws`

Subscribe channels:
- `/topic/queue/{queueId}` — Live queue updates (all users)
- `/user/{userId}/queue/notification` — Near-turn alerts
- `/user/{userId}/queue/token-update` — Token status changes

Using STOMP.js:
```javascript
const client = new Client({
  webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
  onConnect: () => {
    client.subscribe('/topic/queue/1', (msg) => {
      console.log('Queue update:', JSON.parse(msg.body));
    });
  }
});
client.activate();
```

---

## 🧪 Test Scenarios

### Scenario 1 — Multiple users join queue
1. Register 3 users via POST /api/auth/register
2. Each user calls POST /api/queues/join with queueId=1
3. Observe position increments (1, 2, 3)
4. WebSocket subscribers see live update

### Scenario 2 — Staff serves tokens
1. Admin calls POST /api/queues/1/serve-next
2. First waiting token → status = SERVED
3. Second user receives "Near Turn" WebSocket notification
4. Analytics updates served count

### Scenario 3 — No-show handling
1. User joins queue but doesn't respond when called
2. Scheduled job (every 60s) checks tokens older than `no-show-timeout-minutes`
3. Token auto-marked as NO_SHOW
4. Kafka publishes queue.no_show event → Redis invalidated → WS broadcast

### Scenario 4 — VIP priority
1. User joins with priorityType=VIP
2. Their position is computed as half of queue length
3. They're served before normal-priority tokens

---

## 🧠 LLD — Key Classes

### PredictionService
```
estimated_time = ceil(people_ahead / active_staff) * avg_service_time_minutes
```
- Modular: extend with ML model or sliding window average

### QueueService
- createQueue → validates Branch exists → persists
- getQueueSnapshot → reads Redis first, falls back to DB

### TokenService
- joinQueue → rate limit check → capacity check → compute position
  → persist Token → update Redis → publish Kafka event
- serveNextToken → priority-ordered query → update status → notify next user

### RedisCacheService
- Keys: `queue:{id}:position:{userId}`, `queue:{id}:waittime`, `queue:{id}:size`
- TTL: 30 minutes; invalidated on serve/no-show events

---

## 🖼 Screenshots

> _Add screenshots here after running locally_

| Page | Description |
|------|-------------|
| `/login` | JWT login page |
| `/register` | User registration |
| `/` (user) | Active token card + history |
| `/queue/:id` | Queue detail + join button |
| `/admin` | Admin dashboard with live tokens + analytics |

---

## 🚀 Future Improvements

- [ ] SMS/Email notifications via Twilio/SendGrid
- [ ] Machine learning for wait time prediction
- [ ] Mobile app (React Native)
- [ ] Multi-language support (i18n)
- [ ] Advanced analytics with time-series charts
- [ ] Staff performance leaderboard
- [ ] OAuth2 / Social login
- [ ] Kubernetes deployment manifests

---

## 💼 Resume-Ready Description

> **Smart Queue Management System** — Designed and built a production-grade, event-driven queue management platform serving walk-in businesses at scale. Implemented real-time queue tracking using WebSockets (STOMP/SockJS) and Apache Kafka for event streaming across three topics (join/served/no-show). Built a Spring Boot REST API with JWT authentication, role-based access control, Redis caching for O(1) position lookups, MongoDB for event logging, and MySQL for transactional data. Developed a React frontend with real-time updates, admin analytics dashboard, and VIP/emergency priority queuing. Containerized the full stack with Docker Compose spanning MySQL, MongoDB, Redis, and Kafka.

---

## 🐳 Docker Services

| Service | Port | Purpose |
|---------|------|---------|
| MySQL | 3306 | Main relational database |
| MongoDB | 27017 | Event log / analytics |
| Redis | 6379 | Cache layer |
| Zookeeper | 2181 | Kafka coordination |
| Kafka | 9092 | Event streaming |

---

*Built with ❤️ for production-ready systems design*
