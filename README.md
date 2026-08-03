# How SA Are You 🇿🇦

A Spring Boot REST API for a South African trivia game.

## Features

* JWT authentication
* User registration & login
* 5-question timed quiz
* Automatic scoring
* Leaderboard
* Admin question management
* Flyway database migrations
* Docker support
* PostgreSQL (local & Neon)

## Tech Stack

* Java 21
* Spring Boot
* Spring Security
* Spring Data JPA
* PostgreSQL
* Flyway
* Docker
* Maven

## Run Locally

```bash
docker compose up --build
```

Or run directly:

```bash
./mvnw spring-boot:run
```

## API

Main endpoints:

* `POST /api/auth/register`
* `POST /api/auth/login`
* `POST /api/qna/attempts/start`
* `POST /api/qna/attempts/{id}/submit`
* `GET /api/qna/leaderboard`
* `GET /api/qna/questions` (Admin)
* `POST /api/qna/questions` (Admin)

## Environment Variables

```
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
SEED_QUESTIONS
```

## Status
Currently in active development.

Planned additions include:

* Web frontend
* Mobile app
* Profile pictures
* Cloud deployment
* CI/CD
* Additional game modes
