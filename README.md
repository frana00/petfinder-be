# PetSignal Backend

A Spring Boot application for managing pet alerts and notifications.

## Technologies

- Java 21
- Spring Boot 3.2.3
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
    - Update `application.yml` with your AWS credentials and configuration

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
├── alert/            # Alert operations
├── config/           # Configuration classes
├── exception/        # Custom exception classes and handlers
├── photos/           # Photo operations
├── postcodes/        # Postcode operations
├── s3bucket/         # Interaction with AWS s3 bucket
├── scheduler/        # Scheduled jobs
├── user/             # User operations
└── PetSignalApplication.java
```

## Features
- CRUD operations for alerts, photos, users, and subscriptions
- Email and SMS notifications
- OpenAPI documentation


## License

This project is licensed under the MIT License - see the LICENSE file for details.
