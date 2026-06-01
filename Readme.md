# UndoSchool — Global Class Offering Booking System

A production-ready backend service for a global live-learning platform where teachers create class offerings and parents/students book them across different timezones.

---

## Project Overview

This service handles the full lifecycle of online class bookings:

- Teachers register, create courses, define offerings (batches), and schedule individual sessions
- Parents register with their local timezone, browse available offerings, and book them
- All session times are stored in UTC and converted to each user's local timezone on the fly
- Booking conflict detection prevents parents from double-booking overlapping sessions
- Concurrent booking attempts are handled using pessimistic locking at the database level

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.x |
| Database | MySQL |
| ORM | Spring Data JPA / Hibernate |
| Build | Maven |
| Timezone handling | `java.time` (Instant + ZonedDateTime) |

---

## Setup Instructions

### Prerequisites

- Java 21+
- Maven 3.8+
- MySQL 8.0+

### 1. Clone the Repository

```bash
git clone https://github.com/<your-username>/undo-school.git
cd undo-school
```

### 2. Create the Database

```sql
CREATE DATABASE undo_school;
```

### 3. Configure Environment Variables

Copy the example env file and fill in your values:

```bash
cp .env.example .env
```

Or set the following variables in your environment (see full list below).

### 4. Run the Application

```bash
./mvnw spring:boot:run
```

The server starts on `http://localhost:8080` by default.

---

## Environment Variables

| Variable | Description | Example |
|---|---|---|
| `DB_HOST` | MySQL host | `localhost` |
| `DB_PORT` | MySQL port | `3306` |
| `DB_NAME` | Database name | `undo_school` |
| `DB_USERNAME` | MySQL username | `root` |
| `DB_PASSWORD` | MySQL password | `secret` |
| `SERVER_PORT` | (Optional) App port | `8080` |

### `application.properties` template

```properties
spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:undo_school}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
server.port=${SERVER_PORT:8080}
```

---

## API Documentation

> All timestamps in request bodies must be in `LocalDateTime` format: `2025-06-07T18:00:00`
> All timestamps in responses are returned as `ZonedDateTime` strings in the requesting user's local timezone.

---

### Helper Endpoint

#### `GET /timezones`
Returns a sorted list of all valid IANA timezone identifiers (e.g. `Asia/Kolkata`, `America/New_York`).

---

### Teacher APIs

#### Register a Teacher
`POST /teacher/register`

**Request Body:**
```json
{
  "name": "Alice",
  "email": "alice@example.com",
  "timeZone": "America/New_York"
}
```

**Response:** `201 Created`
```
Teacher Profile created with userId: 1 and unique code was: 1234
```
> Save the `userId` and `code` — they are required for all subsequent teacher operations.

---

#### Create a Course
`POST /{id}/{code}/addcourse`

**Request Body:**
```json
{
  "courseName": "Python Coding",
  "teacherName": "Alice"
}
```

**Response:** `201 Created`
```
course added done with reference id: 1
```

---

#### Create an Offering
`POST /{id}/{code}/addoffering`

**Request Body:**
```json
{
  "batchType": "Saturday Batch",
  "courseId": 1,
  "startDate": "2025-06-07T18:00:00",
  "endDate": "2025-07-26T19:00:00"
}
```
> Dates are interpreted in the teacher's registered timezone.

**Response:** `201 Created`
```
Offering added done with ref id: 1
```

---

#### Add a Session to an Offering
`POST /{id}/{code}/session`

**Request Body:**
```json
{
  "offering": 1,
  "startTime": "2025-06-07T18:00:00",
  "endTime": "2025-06-07T19:00:00"
}
```
> Times are interpreted in the teacher's registered timezone. Overlapping sessions for the same teacher are rejected.

**Response:** `201 Created`
```
session added done with ref number: 1
```

---

#### Get All Sessions
`GET /{id}/{code}/session`

Returns all sessions for the teacher, sorted ascending by start time, in the teacher's local timezone.

---

#### Get Upcoming Sessions
`GET /{id}/{code}/upsessions`

Returns only future sessions for the teacher, excluding cancelled offerings.

---

#### Cancel an Offering
`PUT /{id}/{code}/session`

**Request Body:** (plain integer)
```
1
```
> Cancels the offering with the given ID. All associated sessions become inactive.

**Response:** `200 OK`

---

### Parent APIs

#### Register a Parent
`POST /register`

**Request Body:**
```json
{
  "name": "Bob",
  "email": "bob@example.com",
  "timeZone": "Asia/Kolkata"
}
```
> `timeZone` can also be passed via the `X-Timezone` header if omitted from the body. Defaults to `UTC` if neither is provided.

**Response:** `201 Created`

---

#### Browse Available Offerings
`GET /{id}/offerings`

Returns all upcoming confirmed offerings, with dates converted to the parent's registered timezone.

**Response:**
```json
[
  {
    "offeringId": 1,
    "batchType": "Saturday Batch",
    "courseId": 1,
    "startDate": "2025-06-07T23:30:00+05:30[Asia/Kolkata]",
    "endDate": "2025-07-27T00:30:00+05:30[Asia/Kolkata]"
  }
]
```

---

#### Book an Offering
`POST /{id}/booking`

**Request Body:** (plain integer — the offering ID)
```
1
```

- Books the entire offering (all sessions)
- Rejects if any session in this offering overlaps with a session in an already-booked offering
- Handles concurrent booking attempts safely via pessimistic locking

**Response:** `201 Created` — returns the booking ID

---

#### Get My Bookings
`GET /{id}/booking`

Returns all bookings for the parent with their current status.

---

#### Cancel a Booking
`PUT /{id}/booking`

**Request Body:** (plain integer — the booking ID)
```
1
```

Sets the booking status to `cancelled`.

**Response:** `200 OK`

---

## Database Schema Overview

```
Users
  userId (PK), name, email, role (teacher|parent), passcode, timeZone

Course
  id (PK), courseName, teacherName

Offering
  offeringId (PK), batchType, courseId (FK), teacherId (FK → Users),
  offeringStartDate (UTC), offeringEndDate (UTC), status

Sessions
  sessionId (PK), offeringId (FK), teacherId (FK → Users),
  startTime (UTC), endTime (UTC)

Bookings
  bookingId (PK), parentId (FK → Users), offeringId (FK), status
```

All datetime values are stored as `DATETIME` / `TIMESTAMP` in UTC (Java `Instant`). Timezone conversion happens at the service layer using `ZoneId` and `ZonedDateTime`.

---

## Assumptions Made

1. **Authentication is simplified** — teachers authenticate via a plain numeric passcode returned at registration. This is intentional for the scope of this assignment; a real system would use JWT or OAuth2.
2. **One booking per offering per parent** — a parent booking the same offering twice is treated as a conflict at the application layer.
3. **Timezone is set at registration** — a parent or teacher's timezone is fixed at signup and used for all display conversions. There is no per-request timezone override after registration.
4. **Sessions are added individually** — bulk session creation is not exposed (the endpoint is commented out pending refinement).
5. **Offering status drives visibility** — only offerings with `status = confirmed` appear in the parent's browse view.
6. **No authentication middleware** — routes are not secured with Spring Security; the passcode in the URL path acts as a lightweight auth token for teacher endpoints.

---

## Concurrency Handling Approach

The booking flow (`POST /{id}/booking`) uses **pessimistic write locking** at the database level:

1. The parent's `Users` row is fetched with `SELECT ... FOR UPDATE` (via `@Lock(PESSIMISTIC_WRITE)`) inside a `@Transactional` block.
2. All sessions belonging to the desired offering are loaded.
3. Each session is checked against existing confirmed bookings for that parent using a JPQL query that joins `Sessions → Offering → Bookings`.
4. If any overlap is detected, a `BookingException` is thrown and the transaction is rolled back.
5. If no conflict, the booking is persisted.

The pessimistic lock on the user row serializes concurrent booking requests from the same parent, preventing race conditions where two simultaneous requests both pass the conflict check before either commits.

---

## Timezone Handling Approach

- **Storage:** All `startTime`, `endTime`, `offeringStartDate`, and `offeringEndDate` are stored as `java.time.Instant` (UTC epoch) in the database.
- **Teacher input:** When a teacher provides a datetime string (e.g. `2025-06-07T18:00:00`), it is parsed as `LocalDateTime` and converted to `Instant` using the teacher's registered `ZoneId`.
- **Parent display:** When returning sessions or offerings to a parent, the UTC `Instant` is converted to `ZonedDateTime` using the parent's registered `ZoneId`, producing a fully qualified string like `2025-06-07T23:30:00+05:30[Asia/Kolkata]`.
- **Conflict detection:** Because all times are in UTC, overlap checks (`startTime < :endTime AND endTime > :startTime`) work correctly regardless of which timezones the teacher and parent are in.

---

## Running Locally (Step-by-Step)

```bash
# 1. Start MySQL and create the database
mysql -u root -p -e "CREATE DATABASE undo_school;"

# 2. Set environment variables
export DB_USERNAME=root
export DB_PASSWORD=yourpassword
export DB_HOST=localhost
export DB_NAME=undo_school

# 3. Build and run
./mvnw clean spring-boot:run

# 4. Verify the server is up
curl http://localhost:8080/timezones
```

Hibernate will auto-create the schema on first run (`ddl-auto=update`).

---

## Optional Enhancements (Roadmap)

- [ ] Docker + `docker-compose.yml` for one-command local setup
- [ ] Swagger / OpenAPI documentation at `/swagger-ui.html`
- [ ] JWT-based authentication replacing the passcode pattern
- [ ] Bulk session creation endpoint
- [ ] Unit tests for conflict detection and timezone conversion
- [ ] Integration tests using Testcontainers + MySQL
- [ ] CI/CD with GitHub Actions