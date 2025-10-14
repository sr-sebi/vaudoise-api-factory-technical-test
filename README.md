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

## Architecture and design

For this test, I chose to follow my usual approach to building standard boilerplate applications for my usual projects. 
It includes extended JPA management, specifications to allow dynamic filtering and more complex queries, swagger documentation, logging and custom exceptions handling for better error management, and a clear separation of concerns.


The project follows a layered architecture using Spring Boot and JPA to allow for easy management of entities and relationships. 
The domain layer defines abstract Client with Single Table Inheritance, allowing PersonClient and CompanyClient to share the same table but store type-specific fields (birthDate vs companyId).

The service layer handles business logic, validation, and transactional operations, exposing CRUD and paginated/filterable queries via the REST controller layer, which manages API responses consistently with custom exceptions.

Docker Compose manages MariaDB and phpMyAdmin for easy deployment, the latter providing a user-friendly interface for database administration.

---

## ⚙️ Initial Setup

### 1️⃣ Clone the repository

```bash
git clone https://github.com/yourusername/vaudoise-backend.git
cd vaudoise-backend
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

### 3️⃣ Configure Database Connection
Update the `src/main/resources/config/application.yml` file with your database credentials if necessary:
```properties
# MariaDB (Docker)
spring.datasource.url=jdbc:mariadb://localhost:3306/vaudoise_db
spring.datasource.username=vaudoise_user
spring.datasource.password=vaudoise_pass
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
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

---
## 🧠 Notes
- ISO 8601 → LocalDate fields in Spring Boot are automatically serialized in ISO format (YYYY-MM-DD).
- Database is auto-created on application start using spring.jpa.hibernate.ddl-auto=update.
- Initial tables can be defined in docker/init.sql.

---
## 👨‍💻 Author
**Eusebi Franch**

Full Stack Engineer | Mingothings International

eusebi1999@gmail.com