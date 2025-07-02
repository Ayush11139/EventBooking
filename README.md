# Event Ticket Booking System

## Tech Stack
- Java 17, Spring Boot, Spring Data JPA, Postgres, Lombok
- Docker, Docker Compose

**Assumptions:**
- Users are identified by email (no authentication).
- Seats are individually numbered and must be specified when holding/booking.


## Getting Started

### Build & Run with Docker
```sh
gradle build -x test
# or ./gradlew build
# Then build and run with Docker Compose:
docker-compose up --build
```

App will be available at http://localhost:8080

### API Endpoints & Sample cURL

#### 1. Create Event
```sh
curl -X POST http://localhost:8080/api/events \
  -H 'Content-Type: application/json' \
  -d '{"name":"Concert","date":"2024-07-01T19:00:00","location":"Stadium","totalSeats":10}'
```

#### 2. List Events
```sh
curl http://localhost:8080/api/events
```

#### 3. Get Event Details
```sh
curl http://localhost:8080/api/events/1
```

#### 4. Get Event Availability
```sh
curl http://localhost:8080/api/events/1/availability
```

#### 5. Hold Seats
```sh
curl -X POST http://localhost:8080/api/holds \
  -H 'Content-Type: application/json' \
  -d '{"userEmail":"user@example.com","eventId":1,"seatNumbers":[1,2,3]}'
```

#### 6. Confirm Booking
```sh
curl -X POST http://localhost:8080/api/holds/confirm/{holdId}
```

#### 7. View Booking
```sh
curl http://localhost:8080/api/bookings/{bookingId}
```

#### 8. Cancel Booking
```sh
curl -X POST http://localhost:8080/api/bookings/cancel/{bookingId}
```
