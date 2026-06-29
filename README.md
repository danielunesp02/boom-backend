# Boom Backend

Spring Boot backend for the Boom learning platform.

## Stack

- Java 21
- Spring Boot 3.3.x
- Maven
- PostgreSQL
- Flyway
- Redis
- Spring Actuator
- Docker

## Run locally

Start infrastructure:

```bash
cd ../boom-infrastructure
make up
```

Run backend:

```bash
cd ../boom-backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

## Health checks

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/api/v1/system/ping
```
