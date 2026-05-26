# DevOps Release Tracker API

A production-style Java Spring Boot backend for tracking software projects, releases, deployment tasks, rollback notes, and release audit history.

## Why This Project Was Built

DevOps teams need visibility into what is being released, who changed release state, what deployment tasks remain, and how failed releases are documented. This API models that workflow with secure REST endpoints, PostgreSQL persistence, JWT authentication, validation, pagination, audit logging, tests, and Docker support.

## Features

- User registration and login with JWT authentication
- Role-based access with `ADMIN` and `DEVELOPER`
- Project CRUD APIs
- Release creation, listing, filtering, and status transitions
- Release risk scoring based on incomplete tasks, rollback notes, and failed/rolled-back status
- Release approval workflow with admin approve/reject actions before deployment
- CSV export for filtered release reports
- Deployment task creation, completion, and listing by release
- Rollback notes for `FAILED` or `ROLLED_BACK` releases
- Automatic audit log creation whenever release status changes
- Consistent API response envelope
- Global exception handling with meaningful HTTP status codes
- Pagination for list APIs
- Swagger/OpenAPI documentation
- PostgreSQL and Docker Compose setup
- Seed data for quick demos
- Unit and controller tests using JUnit 5, Mockito, and MockMvc

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security with JWT
- PostgreSQL
- Maven
- Lombok
- Jakarta Validation
- Swagger/OpenAPI via Springdoc
- JUnit 5, Mockito, Spring Security Test
- Docker and Docker Compose

## Architecture

The project follows layered architecture:

- `controller`: REST endpoints and HTTP response codes
- `service`: business rules and transactional workflows
- `repository`: Spring Data JPA persistence
- `dto`: request and response objects
- `entity`: JPA entities and enums
- `mapper`: entity-to-DTO mapping
- `exception`: global error handling
- `security`: JWT and user authentication
- `config`: OpenAPI, security, and seed data configuration

## Recent Enhancements

- Added release risk scoring so teams can quickly identify releases with incomplete deployment work, rollback history, or failed status.
- Added an approval workflow that requires admin approval before a release can be marked as `DEPLOYED`.
- Added CSV export for release reports using the same project, status, and date-range filters as the release list API.

## API Endpoints

Authentication:

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/api/auth/register` | Register a user |
| POST | `/api/auth/login` | Login and receive JWT |

Projects:

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/api/projects` | Create project, `ADMIN` |
| GET | `/api/projects` | List projects with pagination |
| GET | `/api/projects/{id}` | Get project by id |
| PUT | `/api/projects/{id}` | Update project, `ADMIN` |
| DELETE | `/api/projects/{id}` | Delete project, `ADMIN` |

Releases:

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/api/projects/{projectId}/releases` | Create release under project |
| GET | `/api/releases` | List/filter releases |
| GET | `/api/releases/export` | Export filtered releases as CSV |
| GET | `/api/releases/{id}` | Get release by id |
| PATCH | `/api/releases/{id}/status` | Update release status and create audit log |
| PATCH | `/api/releases/{id}/risk-score` | Recalculate release risk score |
| PATCH | `/api/releases/{id}/approve` | Approve release, `ADMIN` |
| PATCH | `/api/releases/{id}/reject` | Reject release with reason, `ADMIN` |

Release filters:

```text
GET /api/releases?projectId=1&status=DEPLOYED&fromDate=2026-06-01&toDate=2026-06-30&page=0&size=10
```

CSV export uses the same filters:

```text
GET /api/releases/export?projectId=1&status=DEPLOYED&fromDate=2026-06-01&toDate=2026-06-30
```

Release approval examples:

```bash
curl -X PATCH http://localhost:8080/api/releases/1/approve \
  -H "Authorization: Bearer <admin-token>"
```

```bash
curl -X PATCH http://localhost:8080/api/releases/1/reject \
  -H "Authorization: Bearer <admin-token>" \
  -H "Content-Type: application/json" \
  -d '{"reason":"Smoke tests failed on staging"}'
```

Deployment tasks:

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/api/releases/{releaseId}/tasks` | Add task under release |
| GET | `/api/releases/{releaseId}/tasks` | List tasks by release |
| PATCH | `/api/tasks/{taskId}/complete` | Mark task completed |

Rollback notes:

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/api/releases/{releaseId}/rollback-notes` | Add rollback note |
| GET | `/api/releases/{releaseId}/rollback-notes` | List rollback notes |

Audit logs:

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/api/releases/{releaseId}/audit-logs` | List release audit logs |

## Setup Instructions

Prerequisites:

- Java 17
- Maven
- Docker Desktop, optional but recommended for PostgreSQL

Start PostgreSQL:

```bash
docker compose up -d
```

Run the API:

```bash
mvn spring-boot:run
```

Build and test:

```bash
mvn clean package
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

## Docker Instructions

Start only PostgreSQL:

```bash
docker compose up -d postgres
```

Build the API image:

```bash
docker build -t devops-release-tracker-api .
```

Run the API container:

```bash
docker run --rm -p 8080:8080 --env-file .env devops-release-tracker-api
```

## Environment Variables

| Variable | Default | Description |
| --- | --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/release_tracker` | PostgreSQL JDBC URL |
| `DB_USERNAME` | `release_user` | Database username |
| `DB_PASSWORD` | `release_password` | Database password |
| `JWT_SECRET` | demo secret | JWT signing secret, use a strong value in production |
| `JWT_EXPIRATION_MS` | `86400000` | JWT validity in milliseconds |
| `SERVER_PORT` | `8080` | API server port |
| `JPA_DDL_AUTO` | `update` | Hibernate schema mode |

## Seed Users

The app creates demo users on startup when the database is empty:

| Role | Email | Password |
| --- | --- | --- |
| ADMIN | `admin@releasetracker.dev` | `Admin@123` |
| DEVELOPER | `developer@releasetracker.dev` | `Developer@123` |

## Example Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@releasetracker.dev","password":"Admin@123"}'
```

Use the returned token:

```bash
curl http://localhost:8080/api/projects \
  -H "Authorization: Bearer <token>"
```

## Screenshots

Add screenshots here:

- Swagger UI endpoint list
- Successful JWT login response
- Release status update response
- Release approval response
- CSV export response
- Audit log response after status change

## Resume Bullet Points

- Built a Java 17 Spring Boot 3 REST API for DevOps release tracking with JWT authentication, role-based authorization, PostgreSQL persistence, and Dockerized local infrastructure.
- Implemented release lifecycle workflows including project management, release filtering, approval gates, deployment tasks, rollback notes, and automatic audit logs for status changes.
- Added production-style release risk scoring and CSV report export to support release readiness analysis and operational reporting.
- Designed a clean layered architecture using DTOs, validation, global exception handling, OpenAPI documentation, pagination, and service/controller tests with JUnit 5 and Mockito.
- Packaged the backend with Maven and Docker Compose to support reproducible local development and deployment demonstrations.

## Future Improvements

- Add refresh tokens and token revocation
- Add CI/CD workflow using GitHub Actions
- Add Flyway database migrations
- Add email or Slack notifications for failed deployments
- Add multi-reviewer approval workflow with reviewer assignment
- Add Testcontainers-based PostgreSQL integration tests
