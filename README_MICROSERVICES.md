# Quantity Measurement Microservices Application

This is the microservices-based version of the Quantity Measurement Application, featuring automated service discovery, API gateway routing, and separate auth and measurement services.

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      Frontend (React/Vue)                   │
│              http://localhost:3000                          │
└────────────────┬─────────────────────────────────────────────┘
                 │
┌────────────────▼──────────────────────────────────────────────┐
│                   API Gateway (8080)                         │
│     ▪ Route: /api/v1/auth/** → AUTH-SERVICE                │
│     ▪ Route: /api/v1/quantities/** → MEASUREMENT-SERVICE   │
└────────────────┬─────────────────────────────────────────────┘
                 │
    ┌────────────┴────────────┐
    │                         │
┌───▼──────────────┐   ┌─────▼────────────────────┐
│  AUTH-SERVICE    │   │  MEASUREMENT-SERVICE     │
│    (8081)        │   │       (8082)             │
│ ▪ Register       │   │ ▪ Compare                │
│ ▪ Login          │   │ ▪ Convert                │
│ ▪ Google OAuth   │   │ ▪ Add/Subtract/Divide    │
│ ▪ JWT issuance   │   │ ▪ History & Stats        │
└───┬──────┬───────┘   └──────────────────────────┘
    │      │
    │      └─────────────┐
    │                    │
┌───▼────────┐      ┌────▼────────────────┐
│ MySQL DB   │      │ in-memory H2 DB     │
│ (for users)│      │ (for measurements)  │
└────────────┘      └─────────────────────┘

┌─────────────────────────────────────────┐
│     Eureka Service Registry (8761)      │
│  ▪ Discovers AUTH-SERVICE               │
│  ▪ Discovers MEASUREMENT-SERVICE        │
│  ▪ Discovers API-GATEWAY                │
└─────────────────────────────────────────┘
```

## Prerequisites

- Java 17+
- Maven 3.8+
- MySQL Server (for auth-service, optional)
- Spring Boot 3.2.15
- Spring Cloud 2023.2.5

## Modules

### 1. **eureka-server** (Port 8761)
Service registry for automated discovery of microservices.
- Non-blocking service registration
- Heartbeat monitoring
- Load balancing support

### 2. **api-gateway** (Port 8080)
Spring Cloud Gateway routing requests to appropriate services.
- **Routes:**
  - `/api/v1/auth/**` → `AUTH-SERVICE`
  - `/api/v1/quantities/**` → `MEASUREMENT-SERVICE`
- Service discovery via Eureka
- Stateless

### 3. **auth-service** (Port 8081)
Handles user authentication and authorization.
- **Features:**
  - Local user registration & login (JWT)
  - Google OAuth2 integration
  - User profile management
  - Password encryption (BCrypt)
- **Database:** MySQL (configurable)
- **Depends on:** Eureka Client

### 4. **measurement-service** (Port 8082)
Core measurement operations and calculations.
- **Features:**
  - Unit conversion (Length, Weight, Volume, Temperature)
  - Arithmetic operations (Add, Subtract, Divide, Compare)
  - Operation history & statistics
  - Error tracking
- **Database:** H2 (in-memory for simplicity)
- **Depends on:** Eureka Client, OpenFeign (calls auth-service)

## Setup & Installation

### Step 1: Clone the Repository
```bash
cd QuantityMeasurementApp
```

### Step 2: Configure Secrets Safely

For local development, run the auth service with the `local` profile. That profile allows optional local secret files and safe development defaults.

Create `secrets/oauth-secrets.properties` from `secrets/oauth-secrets.example.properties`:
```properties
spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET
```

Create `secrets/db-secrets.properties` from `secrets/db-secrets.example.properties` if you want local MySQL.

### Step 3: Configure Database

The `auth-service` works out of the box with in-memory H2 for quick local login/signup.
If you want users to survive restarts, add MySQL overrides in `secrets/db-secrets.properties`.

Create `secrets/db-secrets.properties` from the example file:
```properties
AUTH_DB_URL=jdbc:mysql://localhost:3306/quantity_auth_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
AUTH_DB_DRIVER=com.mysql.cj.jdbc.Driver
AUTH_DB_USERNAME=root
AUTH_DB_PASSWORD=your_mysql_password
AUTH_JPA_DIALECT=org.hibernate.dialect.MySQLDialect
```

If you prefer environment variables, the same keys can be exported as `AUTH_DB_URL`, `AUTH_DB_USERNAME`, and `AUTH_DB_PASSWORD`.

### Step 4: Production Secret Management

For Docker, EC2, and CI/CD:
- Do not store secrets in `application.yml`
- Do not bake secrets into Docker images
- Prefer environment variables, AWS Secrets Manager, or AWS SSM Parameter Store
- Use `deploy.env.example` as a reference only
- Set a strong `AUTH_JWT_SECRET` with at least 32 characters

If any real credentials were ever committed or shared, rotate them before deployment.

## Running the Services

Open 4 different terminals and start services in order:

### Terminal 1: Eureka Server
```bash
cd eureka-server
./mvnw spring-boot:run
# or
mvn clean spring-boot:run
```
✅ Access at `http://localhost:8761`

### Terminal 2: AUTH Service
```bash
cd auth-service
./mvnw spring-boot:run
# or
mvn clean spring-boot:run
```
✅ Registers itself at `http://localhost:8761/eureka`

If you launch `auth-service` directly from your IDE instead of Maven, set `SPRING_PROFILES_ACTIVE=local` in the run configuration so it can load the shared `secrets/` files.

### Terminal 3: MEASUREMENT Service
```bash
cd measurement-service
./mvnw spring-boot:run
# or
mvn clean spring-boot:run
```
✅ Registers itself at `http://localhost:8761/eureka`

### Terminal 4: API Gateway
```bash
cd api-gateway
./mvnw spring-boot:run
# or
mvn clean spring-boot:run
```
✅ Starts routing at `http://localhost:8080`

---

## API Endpoints

All endpoints are accessed through the API Gateway at `http://localhost:8080`.

### Authentication Endpoints

#### 1. Register a New User
```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "securepassword",
  "mobileNumber": "1234567890"
}
```
**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 10800,
  "user": {
    "fullName": "John Doe",
    "email": "john@example.com",
    "mobileNumber": "1234567890",
    "role": "USER",
    "authProvider": "LOCAL"
  }
}
```

#### 2. Login
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "securepassword"
}
```

#### 3. Get Current User Profile
```http
GET /api/v1/auth/me
Authorization: Bearer <your_token>
```

#### 4. Google OAuth Login
Redirect to:
```
GET /oauth2/authorization/google
```

---

### Measurement Endpoints (Auth Required)

#### 1. Compare Quantities
```http
POST /api/v1/quantities/compare
Authorization: Bearer <your_token>
Content-Type: application/json

{
  "thisQuantityDTO": {"value": 1, "unit": "METER", "measurementType": "Length"},
  "thatQuantityDTO": {"value": 100, "unit": "CM", "measurementType": "Length"}
}
```
**Response:**
```json
{
  "id": 1,
  "thisValue": 1,
  "thisUnit": "METER",
  "thisMeasurementType": "Length",
  "thatValue": 100,
  "thatUnit": "CM",
  "operation": "compare",
  "resultString": "true",
  "isError": false,
  "createdAt": "2025-01-15T10:30:00"
}
```

#### 2. Convert Unit
```http
POST /api/v1/quantities/convert
Authorization: Bearer <your_token>
Content-Type: application/json

{
  "thisQuantityDTO": {"value": 5, "unit": "KM", "measurementType": "Length"},
  "thatQuantityDTO": {"value": 0, "unit": "METER", "measurementType": "Length"}
}
```

#### 3. Add Quantities
```http
POST /api/v1/quantities/add
Authorization: Bearer <your_token>
Content-Type: application/json

{
  "thisQuantityDTO": {"value": 500, "unit": "GRAM", "measurementType": "Weight"},
  "thatQuantityDTO": {"value": 1, "unit": "KG", "measurementType": "Weight"}
}
```

#### 4. Subtract Quantities
```http
POST /api/v1/quantities/subtract
Authorization: Bearer <your_token>
Content-Type: application/json

{
  "thisQuantityDTO": {"value": 5, "unit": "LITER", "measurementType": "Volume"},
  "thatQuantityDTO": {"value": 2000, "unit": "ML", "measurementType": "Volume"}
}
```

#### 5. Divide Quantities
```http
POST /api/v1/quantities/divide
Authorization: Bearer <your_token>
Content-Type: application/json

{
  "thisQuantityDTO": {"value": 100, "unit": "INCH", "measurementType": "Length"},
  "thatQuantityDTO": {"value": 5, "unit": "CM", "measurementType": "Length"}
}
```

#### 6. Get Operation History
```http
GET /api/v1/quantities/history/operation/compare
Authorization: Bearer <your_token>
```

#### 7. Get Error History
```http
GET /api/v1/quantities/history/errored
Authorization: Bearer <your_token>
```

#### 8. Get Operation Count
```http
GET /api/v1/quantities/count/add
Authorization: Bearer <your_token>
```

---

## Supported Units

### Length
- `MM`, `CM`, `METER`, `KM`, `INCH`, `FOOT`, `YARD`, `MILE`

### Weight
- `MG`, `GRAM`, `KG`, `OUNCE`, `POUND`

### Volume
- `ML`, `LITER`, `GALLON`, `PINT`, `CUBIC_METER`

### Temperature
- `CELSIUS`, `FAHRENHEIT`, `KELVIN`
- ⚠️ Note: Temperature does not support arithmetic (ADD/SUBTRACT)

---

## Service Communication

### Inter-Service Communication (OpenFeign)

The **measurement-service** uses **OpenFeign** to validate user tokens with **auth-service**:

```java
@FeignClient(name = "auth-service", path = "/api/v1/auth")
public interface AuthClient {
    @GetMapping("/me")
    UserProfileResponse me();
}
```

Every measurement operation calls `authClient.me()` to validate the JWT token.

---

## Configuration Files

### eureka-server/application.yml
```yaml
server:
  port: 8761
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

### api-gateway/application.yml
```yaml
server:
  port: 8080
spring:
  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: lb://AUTH-SERVICE
          predicates:
            - Path=/api/v1/auth/**
        - id: measurement-service
          uri: lb://MEASUREMENT-SERVICE
          predicates:
            - Path=/api/v1/quantities/**
```

### auth-service/application.yml
```yaml
server:
  port: 8081
spring:
  application:
    name: auth-service
  datasource:
    url: jdbc:h2:mem:authdb (default) or jdbc:mysql://localhost:3306/quantity_auth_db...
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

### measurement-service/application.yml
```yaml
server:
  port: 8082
spring:
  application:
    name: measurement-service
  datasource:
    url: jdbc:h2:mem:measurementdb
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

---

## Troubleshooting

### Services not registering with Eureka
- Ensure Eureka server is running first
- Check `eureka.client.service-url.defaultZone` in each service's `application.yml`

### 401 Unauthorized on measurement endpoints
- Register/Login first to obtain a token
- Include `Authorization: Bearer <token>` header in requests

### Database connection errors
- If you are using the default H2 setup, no database setup is needed
- If you are using MySQL, ensure MySQL is running before starting `auth-service`
- Check `secrets/db-secrets.properties` or the `AUTH_DB_*` environment variables

### Port conflicts
- Change port in respective `application-*.yml` files
- Update `api-gateway` routes accordingly

---

## Development Notes

- Each service is independently deployable
- Services communicate via REST + OpenFeign
- Eureka provides dynamic service discovery
- API Gateway provides a single entry point
- JWT tokens are validated by auth-service
- All operations persist to respective databases

---

## Next Steps

1. **Add authentication interceptor** for automatic token propagation
2. **Implement circuit breakers** (Hystrix/Resilience4j) for fault tolerance
3. **Add logging aggregation** (ELK stack or Splunk)
4. **Configure Docker** for containerization
5. **Set up CI/CD** (GitHub Actions, Jenkins)
6. **Add API rate limiting** in the gateway

---

## License

This project is part of the Spring Boot Microservices Learning Content.

---

## Support

For issues or questions, refer to the inline code comments or update the README accordingly.
