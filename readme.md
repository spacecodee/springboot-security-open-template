# A Spring security project with JWT authentication template for Spring Boot 3.3.5

## What is mandatory to run this project?

- Java 23
- PostgreSQL
- Create a application-local.properties file in the resources folder with the following content:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password
security.jwt.expiration-in-minutes=#expiration time in minutes
security.jwt.secret-key=#your secret key
security.default.roles=#admin role
security.default.role=#user role
```

- An IDE: IntelliJ IDEA, Eclipse, etc.