# Global Class Offering Booking System

A production-ready backend service for a global live-learning platform where teachers conduct online classes for students across different countries and timezones.

**Live API:** [https://undo-school-l2uj.onrender.com](https://undo-school-l2uj.onrender.com)

**Swagger UI:** [https://undo-school-l2uj.onrender.com/swagger-ui/index.html](https://undo-school-l2uj.onrender.com/swagger-ui/index.html)

---

## Tech Stack

- **Java 21** + **Spring Boot 4.0.6**
- **PostgreSQL** — primary database
- **Spring Data JPA** + **Hibernate** — ORM
- **Springdoc OpenAPI (Swagger)** — API documentation
- **Docker** — containerization
- **GitHub Actions** — CI pipeline
- **Render** — CD and deployment

---

## Core Concepts

The system models three layers:

**Course** → a subject like Python Coding or Art Drawing

**Offering** → a schedulable batch of that course (e.g. Saturday Batch, Summer Camp). Each offering has a teacher, a start date, and an end date.

**Session** → an individual class meeting within an offering (e.g. June 7, 7:30 PM – 9:00 PM)

Parents book at the offering level — booking one offering books all its sessions together.

---

## Key Engineering Decisions

### Timezone Handling

All session times are stored in UTC as `Instant` in the database. Teachers create sessions in their local timezone — the backend converts to UTC before storing. When parents view sessions, UTC is converted to their local timezone for display.

```
Teacher (IST) → backend converts → UTC stored in DB
                                          ↓
Parent (EST)  ← backend converts ← UTC read from DB
```

No timezone data is lost. One universal source of truth.

### Concurrent Booking Prevention

Booking conflict detection happens at the **database level**, not in application memory. A JPQL query checks for overlapping session times across the parent's existing confirmed bookings in a single atomic operation.

To prevent race conditions where two simultaneous booking requests both pass the conflict check before either commits, the system uses **pessimistic locking** (`PESSIMISTIC_WRITE`) on the parent's user row. This locks the row for the duration of the transaction, forcing concurrent requests to queue instead of running in parallel.

### Conflict Detection Logic

Two sessions overlap when:
```
newSession.startTime < existingSession.endTime
AND
newSession.endTime > existingSession.startTime
```

This check runs at DB level for both teacher scheduling (preventing a teacher from double-booking themselves) and parent booking (preventing a parent from booking overlapping offerings).

---

## Database Schema

```
users         → userId, name, email, role, passcode, timeZone
courses       → id, name
offerings     → offeringId, batchType, courseId, teacherId, offeringStartDate, offeringEndDate, status
sessions      → sessionId, offeringId, teacherId, startTime, endTime
bookings      → bookingId, parentId, offeringId, status
```

`status` is an enum: `conformed` (active) or `cancelled`

---

## API Reference

Full interactive documentation available at Swagger UI. Summary below.

### Utility

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/timezones` | Returns all valid IANA timezone strings |

### Teacher APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/teacher/register` | Register as a teacher. Returns userId and passcode |
| POST | `/{id}/{code}/addcourse` | Create a new course |
| POST | `/{id}/{code}/addoffering` | Create an offering under a course |
| POST | `/{id}/{code}/session` | Add a session to an offering |
| GET | `/{id}/{code}/session` | Get all sessions (converted to teacher's timezone) |
| GET | `/{id}/{code}/upsessions` | Get upcoming sessions only (excludes cancelled offerings) |
| PUT | `/{id}/{code}/offering` | Cancel an offering |

### Parent APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/register` | Register as a parent |
| GET | `/{id}/offerings` | View available offerings (shown in parent's timezone) |
| POST | `/{id}/booking` | Book an offering (all sessions booked together) |
| GET | `/{id}/booking` | View all bookings |
| PUT | `/{id}/booking` | Cancel a booking |

---

## Authentication

This API uses a simple passcode-based identity system. When a teacher registers, a unique passcode is generated and returned. Teachers pass `{userId}` and `{passcode}` as path variables on every protected endpoint.

Parents are identified by `{userId}` only (no passcode required for parent endpoints).

---

## Sample Request Flow

**1. Register as teacher (auto-detects timezone from X-Timezone header)**
```
POST /teacher/register
X-Timezone: Asia/Kolkata

{
  "name": "Ravi Kumar",
  "email": "ravi@example.com"
}

Response: "Teacher Profile created with userId: 1 and unique code was: 1423"
```

**2. Add a course**
```
POST /1/1423/addcourse

{ "name": "Python Coding" }
```

**3. Add an offering**
```
POST /1/1423/addoffering

{
  "batchType": "Saturday Batch",
  "courseId": 1,
  "startTime": "2026-06-07T19:30:00",
  "endTime": "2026-09-27T21:00:00"
}
```

**4. Add sessions to the offering**
```
POST /1/1423/session

{
  "offeringId": 1,
  "startTime": "2026-06-07T19:30:00",
  "endTime": "2026-06-07T21:00:00"
}
```

**5. Parent registers from USA**
```
POST /register
X-Timezone: America/New_York

{
  "name": "John Smith",
  "email": "john@example.com"
}
```

**6. Parent views offerings (sessions shown in EST)**
```
GET /2/offerings
```

**7. Parent books an offering**
```
POST /2/booking

1
```

---

## Running Locally

**Prerequisites:** Java 21, Maven, PostgreSQL

**1. Clone the repository**
```bash
git clone https://github.com/nishithsai00/undo-school
cd undo-school
```

**2. Configure database**

Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/undoschool
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.jdbc.time_zone=UTC
```

**3. Run**
```bash
mvn spring-boot:run
```

API available at `http://localhost:8080`

Swagger UI at `http://localhost:8080/swagger-ui/index.html`

---

## Running with Docker

```bash
docker build -t undo-school .
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/undoschool \
  -e SPRING_DATASOURCE_USERNAME=user \
  -e SPRING_DATASOURCE_PASSWORD=pass \
  undo-school
```

---

## CI/CD Pipeline

**CI — GitHub Actions**

On every push, the pipeline:
1. Checks out the code
2. Sets up Java 21 (Temurin)
3. Runs `mvn clean install -DskipTests`

Pipeline config: `.github/workflows/build.yml`

**CD — Render**

The application is deployed on Render using the included Dockerfile. Render automatically redeploys on every push to the main branch.

Live URL: [https://undo-school-l2uj.onrender.com](https://undo-school-l2uj.onrender.com)

---

## Error Handling

All errors return a structured JSON response:

```json
{
  "status": 409,
  "message": "Session on 2026-06-14T17:00:00Z already exists in your previous booking",
  "timestamp": 1748789400000
}
```

| Exception | HTTP Status |
|-----------|-------------|
| Booking conflict | 409 Conflict |
| Invalid user | 401 Unauthorized |
| Invalid passcode | 401 Unauthorized |
| Offering not found | 404 Not Found |
| Invalid timezone | 404 Not Found |
| Session overlap | 409 Conflict |
| Empty name/email | 204 No Content |

---

## Author

**Nishith Sai**
GitHub: [github.com/nishithsai00](https://github.com/nishithsai00)
LinkedIn: [linkedin.com/in/nishith-sai-62a60b378](https://linkedin.com/in/nishith-sai-62a60b378)
