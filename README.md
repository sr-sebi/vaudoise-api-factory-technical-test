# 🏦 Vaudoise Backend

Backend application built with **Spring Boot 3**, **Java 21**, and **MariaDB**, designed to manage clients (individuals and companies).  
Includes **Docker Compose** integration to easily spin up the database and **phpMyAdmin** for visual administration.

---

## 🚀 Main Technologies

| Component | Version / Description |
|-----------|----------------------|
| **Java** | 21 |
| **Spring Boot** | 3.5.6 |
| **Database** | MariaDB (Docker) |
| **ORM** | Hibernate / JPA |
| **DB Admin** | phpMyAdmin |
| **Dependency Manager** | Maven |
| **Containers** | Docker Compose |
| **API Documentation** | Swagger UI |

## Architecture and design

For this test, I chose to follow my usual approach to building standard boilerplate applications for my usual projects. 
It includes extended JPA management, specifications to allow dynamic filtering and more complex queries, swagger documentation, logging and custom exceptions handling for better error management, and a clear separation of concerns.

The project follows a layered architecture using Spring Boot and JPA to allow for easy management of entities and relationships. 
The domain layer defines abstract Client with Single Table Inheritance, allowing PersonClient and CompanyClient to share the same table but store type-specific fields (birthDate vs companyId).

The service layer handles business logic, validation, and transactional operations, exposing CRUD and paginated/filterable queries via the REST controller layer, which manages API responses consistently with custom exceptions.

Docker Compose manages MariaDB and phpMyAdmin for easy deployment, the latter providing a user-friendly interface for database administration.

- Config:
  - Logging:
    - LogServiceMethod: Logs entry and exit of service methods with parameters and execution time.
    - RequestLoggingFilter: Logs incoming HTTP requests.
  - Security:
    - SecurityConfig: Basic authentication for Swagger UI.
    - WebConfig: Allows paging, sorting and serialization.
- Exception:
  - CustomException: Base class for custom exceptions.
  - ErrorEnum: Standardized error codes and messages.
  - GlobalExceptionHandler: Centralized exception handling for consistent API error responses.
- Persistence:
  - Entities: Client (abstract), PersonClient, CompanyClient.
    - Auditable: Base class for entities with createdAt and updatedAt timestamps.
  - Repositories: ClientRepository with JpaSpecificationExecutor for dynamic queries.
  - Service: Business logic and validation.
  - Specification: Dynamic filtering for queries.
- Rest:
  - Controllers: REST endpoints for CRUD operations and filtering.
  - DTOs: Data Transfer Objects for API requests and responses.

## Why it works?
- **Simplicity**: Clear separation of concerns with well-defined layers (Controller, Service, Repository).
- **Maintainability**: Use of DTOs, custom exceptions, and centralized logging makes the codebase easy to maintain and extend.
- **Scalability**: Dynamic filtering with Specifications allows for flexible querying as requirements evolve.
- **User-Friendly**: Swagger UI provides an interactive API documentation and testing interface.
- **Ease of Deployment**: Docker Compose simplifies the setup of the database and admin interface.
- **Auditability**: Creation and update are tracked with timestamps.

You can easily test the application using the provided Swagger UI or any API client like Postman.

---

## ⚙️ Initial Setup

### 1️⃣ Clone the repository

```bash
git clone https://github.com/sr-sebi/vaudoise-api-factory-technical-test.git
cd vaudoise-api-factory-technical-test
```

### 2️⃣ Configure the Database

the project includes a docker-compose.yml which automatically launches:
- MariaDB on port 3306
- phpMyAdmin on port 9002

Run:
``
docker-compose up -d
``
✅ This will create the containers and the database vaudoise_db with initial tables.


If you need to reset the database, run:
```
docker compose down -v
docker compose up
```

### 3️⃣ Configure Database Connection
Update the `src/main/resources/config/appplication.properties` file with your database credentials if necessary:
```properties
# MariaDB (Docker)
spring.datasource.url=jdbc:mariadb://localhost:3306/vaudoise_db
spring.datasource.username=vaudoise_user
spring.datasource.password=vaudoise_pass
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect

# Server
server.port=8080
server.servlet.context-path=/api
```

### 4️⃣ Build and Run the Application
Use Maven to build and run the application:
```bash
mvn clean install
mvn spring-boot:run
```

---
## 🌐 Quick Access
- **API Base URL**: `http://localhost:8080/api`


- **phpMyAdmin**: `http://localhost:9002`
  - Username: `vaudoise_user`
  - Password: `vaudoise_pass`


- **MariaDB**: `localhost:3306`
  - Database: `vaudoise_db`
  - User: `vaudoise_user`
  - Password: `vaudoise_pass`


- **Swagger UI**: `http://localhost:8080/api/swagger-ui/index.html`
  - User: `vaudoise_user`
  - Password: `vaudoise_pass`

---
## 🧠 Notes
- ISO 8601 → LocalDate fields in Spring Boot are automatically serialized in ISO format (YYYY-MM-DD).
- Database is auto-created on application start.
- Initial tables can be defined in docker/init.sql.
- Swagger UI is protected with basic auth for security.
- Audit fields (createdAt, updatedAt) are automatically managed.

---
## 👨‍💻 Author
**Eusebi Franch**

Full Stack Engineer | Mingothings International

eusebi1999@gmail.com