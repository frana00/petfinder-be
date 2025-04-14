# PetSignal Backend

A Spring Boot application for managing pet alerts and notifications.

## Technologies

- Java 21
- Spring Boot 3.2.3
- Spring Security with JWT
- Spring Data JPA
- MySQL
- Maven
- Lombok
- OpenAPI/Swagger

## Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher

## Setup

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/petsignal-be.git
   cd petsignal-be
   ```

2. Create a MySQL database:
   ```
   CREATE DATABASE petsignal;
   ```

3. Configure the application:
   - Update `application.yml` with your database credentials
   - Set environment variables for sensitive information:
     ```
     export JWT_SECRET=your-secret-key
     export MAIL_USERNAME=your-email@gmail.com
     export MAIL_PASSWORD=your-app-password
     ```

4. Build the application:
   ```
   mvn clean install
   ```

5. Run the application:
   ```
   mvn spring-boot:run
   ```

## API Documentation

Once the application is running, you can access the API documentation at:
- Swagger UI: http://localhost:8080/api/v1/swagger-ui.html

## Project Structure

```
src/main/java/com/petsignal/
├── config/           # Configuration classes
├── controller/       # REST controllers
├── dto/              # Data Transfer Objects
├── entity/           # JPA entities
├── exception/        # Custom exceptions and handlers
├── repository/       # JPA repositories
├── security/         # Security related classes
├── service/          # Business logic
└── PetSignalApplication.java
```

## Features

- User authentication and authorization with JWT
- CRUD operations for alerts, photos, users, and subscriptions
- Email and SMS notifications
- OpenAPI documentation
- Global exception handling
- Data validation

## License

This project is licensed under the MIT License - see the LICENSE file for details.
