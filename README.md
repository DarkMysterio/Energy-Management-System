# Distributed System - Energy Management System

## Project Overview

This project implements a distributed system for managing users, devices, and authentication in an energy management context. The system follows a microservices architecture with three main backend services, a reverse proxy, separate databases, and a React frontend.

## System Architecture

### Microservices Architecture
```
┌─────────────────┐
│  React Frontend │ (Port 3000)
│  (localhost)    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Traefik Proxy  │ (Port 80 & 8080)
│  Reverse Proxy  │
└────────┬────────┘
         │
    ┌────┴────┬─────────────┬─────────────┐
    ▼         ▼             ▼             ▼
┌─────────┐ ┌─────────┐ ┌──────────┐ ┌──────────┐
│  Auth   │ │  User   │ │  Device  │ │ Database │
│ Service │ │ Service │ │ Service  │ │ Services │
│(8082)   │ │ (8080)  │ │ (8081)   │ │          │
└────┬────┘ └────┬────┘ └────┬─────┘ └──────────┘
     │           │            │
     ▼           ▼            ▼
┌─────────┐ ┌─────────┐ ┌──────────┐
│ db-auth │ │db-users │ │db-devices│
│(5435)   │ │(5433)   │ │(5434)    │
└─────────┘ └─────────┘ └──────────┘
```

## Components

### 1. Authentication Service (Port 8082)
**Location:** `auth-service/auth-service/`

**Technologies:**
- Spring Boot
- Spring Security
- JWT (JSON Web Tokens)
- PostgreSQL
- Docker

**Key Features:**
- User registration and login
- JWT token generation and validation
- Password encryption
- Role-based access control (ADMIN/CLIENT)
- Token validation endpoint for API gateway

**Main Components:**
- `AuthController`: Handles registration, login, and token validation
- `AuthService`: Business logic for authentication
- `JWTService`: Token generation and validation
- `JwtFilter`: Request filtering and authorization
- `SecurityConfig`: Spring Security configuration
- `AuthDetails` entity: Stores user credentials and roles

**Endpoints:**
- `POST /auth/register` - User registration
- `POST /auth/login` - User login
- `GET /auth/validate` - Token validation (used by Traefik)

**Database:** PostgreSQL (`db-auth`, Port 5435)

---

### 2. User Service (Spring Demo - Port 8080)
**Location:** `ds2025_spring_example/demo/`

**Technologies:**
- Spring Boot
- Spring Data JPA
- PostgreSQL
- REST API
- Docker

**Key Features:**
- User CRUD operations (Create, Read, Update, Delete)
- Age validation using custom annotations
- Login functionality
- Integration with Device Service
- User cache management
- Cross-origin resource sharing (CORS) enabled

**Main Components:**
- `PersonController`: REST endpoints for user management
- `PersonService`: Business logic
- `PersonRepository`: Data access layer
- `Person` entity: User data model
- `DataInitializer`: Populates initial data
- Custom validators: `AgeValidator`, `@AgeLimit` annotation

**Endpoints:**
- `GET /people` - Get all users
- `POST /people` - Create new user
- `GET /people/{id}` - Get user by ID
- `PUT /people/{id}` - Update user
- `DELETE /people/{id}` - Delete user
- `DELETE /people` - Delete all users
- `POST /people/login` - User login

**Database:** PostgreSQL (`db-users`, Port 5433)

---

### 3. Device Service (Port 8081)
**Location:** `device-service/`

**Technologies:**
- Spring Boot
- Spring Data JPA
- PostgreSQL
- REST API
- Docker

**Key Features:**
- Device CRUD operations
- User-device assignment management
- User cache synchronization
- Partial updates (PATCH) for device properties
- Integration with User Service

**Main Components:**
- `DeviceController`: Device management endpoints
- `UserAndDeviceController`: User-device relationship management
- `UserCacheController`: User cache operations
- `DeviceService`: Business logic for devices
- `Device` entity: Device data model
- `UserAndDevice` entity: Many-to-many relationship
- `UserCache` entity: Cached user data

**Endpoints:**

**Device Management:**
- `GET /devices` - Get all devices
- `POST /devices` - Create new device
- `PUT /devices/{id}` - Update device
- `DELETE /devices` - Delete all devices
- `PATCH /devices/name/{id}` - Update device name
- `PATCH /devices/consumption/{id}` - Update device consumption

**User-Device Assignment:**
- `GET /userAndDevice` - Get all user-device assignments
- `POST /userAndDevice` - Assign device to user
- `DELETE /userAndDevice/{userId}/{deviceId}` - Remove assignment
- `GET /userAndDevice/user/{userId}` - Get devices for specific user

**User Cache:**
- `GET /userCache` - Get all cached users
- `POST /userCache/sync` - Synchronize user data from User Service

**Database:** PostgreSQL (`db-devices`, Port 5434)

---

### 4. Traefik Reverse Proxy (Port 80, Dashboard: 8080)
**Configuration Files:**
- `traefik.yml`: Main Traefik configuration
- `dynamic/path.yml`: Dynamic routing rules

**Key Features:**
- API Gateway functionality
- Path-based routing
- CORS middleware
- JWT authentication forwarding
- Path rewriting
- Service load balancing
- Access logging

**Routing Rules:**
```
/auth/*           → auth-service:8082
/api/users/*      → spring-demo:8080/people/*    (with auth)
/api/devices/*    → device-service:8081/devices/* (with auth)
```

**Middlewares:**
- **CORS**: Enables cross-origin requests from frontend
- **Auth Forward**: Validates JWT tokens with auth-service
- **Path Rewrite**: Transforms external URLs to internal service paths

---

### 5. Frontend (React - Port 3000)
**Location:** `frontend/`

**Technologies:**
- React 18
- React Router DOM
- Axios (for API calls)
- CSS

**Key Features:**
- JWT-based authentication
- Role-based UI (ADMIN vs CLIENT views)
- User management (ADMIN only)
- Device management (ADMIN only)
- Device assignment to users (ADMIN only)
- Client device view (CLIENT role)
- Responsive navigation
- Token storage and management

**Pages:**
- `LoginPage`: User authentication
- `UsersPage`: User management (ADMIN)
- `DevicesPage`: Device management (ADMIN)
- `AssignDevicePage`: Assign devices to users (ADMIN)
- `ClientDevicesPage`: View assigned devices (CLIENT)

**Services:**
- `authService.js`: JWT token management, login/logout
- `api.js`: API communication with backend services

**User Roles:**
- **ADMIN**: Full access to users, devices, and assignments
- **CLIENT**: View only their assigned devices

---

## Database Schema

### 1. Auth Database (`authdb`)
**Table: auth_details**
- `id` (UUID, Primary Key)
- `username` (String, Unique)
- `password` (String, Encrypted)
- `role` (String: ADMIN/CLIENT)
- `user_id` (UUID, References User Service)

### 2. Users Database (`example-db`)
**Table: person**
- `id` (UUID, Primary Key)
- `name` (String)
- `address` (String)
- `age` (Integer)
- `role` (String)

### 3. Devices Database (`devices`)
**Table: device**
- `id` (UUID, Primary Key)
- `name` (String)
- `description` (String)
- `address` (String)
- `max_hourly_consumption` (Double)

**Table: user_and_device**
- `id` (UUID, Primary Key)
- `user_id` (UUID)
- `device_id` (UUID, Foreign Key → device)

**Table: user_cache**
- `id` (UUID, Primary Key)
- `user_id` (UUID)
- `name` (String)

---

## Setup and Deployment

### Prerequisites
- Docker & Docker Compose
- Java 17+
- Maven
- Node.js & npm
- PostgreSQL (via Docker)

### Running the Application

#### 1. Build and Start All Services
```powershell
docker-compose up --build
```

This will start:
- Traefik Reverse Proxy
- Auth Service
- User Service (spring-demo)
- Device Service
- 3 PostgreSQL databases

#### 2. Start Frontend
```powershell
cd frontend
npm install
npm start
```

Frontend will be available at: `http://localhost:3000`

#### 3. Access Points
- **Frontend**: http://localhost:3000
- **Traefik Dashboard**: http://localhost:8080
- **API Gateway**: http://localhost:80
- **Auth Service** (internal): http://localhost:8082
- **User Service** (internal): http://localhost:8080
- **Device Service** (internal): http://localhost:8081

### API Documentation
- **Auth Service Swagger**: http://localhost:8082/swagger-ui/index.html
- **User Service Swagger**: http://localhost:8080/swagger-ui/index.html
- **Device Service Swagger**: http://localhost:8081/swagger-ui/index.html

### Database Ports (for direct access)
- Auth DB: `localhost:5435`
- Users DB: `localhost:5433`
- Devices DB: `localhost:5434`

**Credentials:** 
- Username: `postgres`
- Password: `1163`

---

## API Flow

### 1. User Registration/Login
```
Frontend → POST /auth/register or /auth/login
       ↓
Traefik (no auth required)
       ↓
Auth Service → Validate credentials
       ↓
Return JWT token
```

### 2. Protected API Calls
```
Frontend → API call with JWT in Authorization header
       ↓
Traefik → Forward auth to /auth/validate
       ↓
Auth Service → Validate JWT
       ↓
If valid: Route to appropriate service
If invalid: Return 401 Unauthorized
```

### 3. User Management (Admin)
```
Frontend → GET/POST/PUT/DELETE /api/users/*
       ↓
Traefik → Validate JWT → Rewrite path
       ↓
User Service (/people/*)
```

### 4. Device Management (Admin)
```
Frontend → GET/POST/PUT/DELETE /api/devices/*
       ↓
Traefik → Validate JWT → Rewrite path
       ↓
Device Service (/devices/*)
```

---

## Key Features Implemented

### Authentication & Authorization
- ✅ JWT-based authentication
- ✅ Role-based access control (ADMIN/CLIENT)
- ✅ Secure password storage (encryption)
- ✅ Token validation middleware
- ✅ Protected API routes

### User Management
- ✅ Create, Read, Update, Delete users
- ✅ Age validation
- ✅ User login functionality
- ✅ Admin panel for user management

### Device Management
- ✅ Create, Read, Update, Delete devices
- ✅ Partial updates (name, consumption)
- ✅ Device assignment to users
- ✅ View devices by user
- ✅ User cache synchronization

### Microservices Communication
- ✅ Service-to-service REST API calls
- ✅ User Service ↔ Device Service integration
- ✅ Auth Service validation for all protected routes
- ✅ User cache mechanism for performance

### API Gateway
- ✅ Centralized routing via Traefik
- ✅ CORS handling
- ✅ Path rewriting
- ✅ Authentication forwarding
- ✅ Access logging

### Documentation
- ✅ Swagger/OpenAPI 3.0 for all services
- ✅ Interactive API testing interface

### Frontend
- ✅ React single-page application
- ✅ Role-based UI rendering
- ✅ JWT token management
- ✅ CRUD operations for users and devices
- ✅ Device assignment interface
- ✅ Client device view

---

## Configuration

### Environment Variables

**Auth Service:**
```env
DB_IP=db-auth
DB_PORT=5432
DB_DBNAME=authdb
DB_USER=postgres
DB_PASSWORD=1163
USER_SERVICE_URL=http://spring-demo:8080
SERVER_PORT=8082
```

**User Service:**
```env
DB_IP=db-users
DB_PORT=5432
DB_DBNAME=example-db
DB_USER=postgres
DB_PASSWORD=1163
DEVICE_SERVICE_URL=http://device-service:8081
SERVER_PORT=8080
```

**Device Service:**
```env
DB_IP=db-devices
DB_PORT=5432
DB_DBNAME=devices
DB_USER=postgres
DB_PASSWORD=1163
USER_SERVICE_URL=http://spring-demo:8080
SERVER_PORT=8081
```

---

## Testing

### Using Postman
Import the collection file:
- `ds2025_spring_example/demo/postman_collection.json`

### Manual Testing Flow
1. Register a new user (ADMIN role)
2. Login to get JWT token
3. Use token in Authorization header for protected endpoints
4. Create devices
5. Assign devices to users
6. Login as CLIENT and view assigned devices

---

## Design Patterns Used

### Backend
- **Repository Pattern**: Data access abstraction
- **Service Layer Pattern**: Business logic separation
- **DTO Pattern**: Data transfer between layers
- **Builder Pattern**: Object construction (PersonBuilder)
- **Dependency Injection**: Spring Framework
- **Filter Pattern**: JWT authentication filter
- **Converter Pattern**: Entity ↔ DTO conversion

### Frontend
- **Component Pattern**: React components
- **Service Pattern**: API and auth services
- **State Management**: React hooks (useState, useEffect)
- **Protected Routes**: Authentication guards

---

## Security Features

1. **JWT Authentication**: Stateless token-based auth
2. **Password Encryption**: Secure password storage
3. **CORS Configuration**: Controlled cross-origin access
4. **Role-Based Access**: ADMIN vs CLIENT permissions
5. **Token Validation**: All API calls validated
6. **Secure Headers**: Traefik middleware configuration

---

## Technologies Summary

### Backend Stack
- **Framework**: Spring Boot 3.x
- **Language**: Java 17
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security + JWT
- **Build Tool**: Maven
- **Containerization**: Docker

### Frontend Stack
- **Framework**: React 18
- **Routing**: React Router DOM v6
- **HTTP Client**: Fetch API
- **Styling**: CSS
- **Build Tool**: Create React App

### Infrastructure
- **Reverse Proxy**: Traefik v3.2
- **Container Orchestration**: Docker Compose
- **Database**: PostgreSQL (3 instances)
- **Networking**: Docker networks
- **API Documentation**: Swagger/OpenAPI 3.0

---

## Project Structure

```
lab/
├── auth-service/              # Authentication microservice
│   └── auth-service/
│       ├── src/main/java/
│       ├── Dockerfile
│       └── pom.xml
├── device-service/            # Device management microservice
│   ├── src/main/java/
│   ├── Dockerfile
│   └── pom.xml
├── ds2025_spring_example/     # User management microservice
│   └── demo/
│       ├── src/main/java/
│       ├── Dockerfile
│       └── pom.xml
├── frontend/                  # React frontend
│   ├── src/
│   ├── public/
│   └── package.json
├── dynamic/                   # Traefik dynamic config
│   └── path.yml
├── logs/                      # Traefik access logs
├── docker-compose.yml         # Container orchestration
├── traefik.yml               # Traefik static config
└── README.md                 # This file
```

---

## Future Enhancements

### Potential Improvements
- [ ] Real-time device monitoring
- [ ] WebSocket support for live updates
- [ ] Message queue (RabbitMQ/Kafka) for async communication
- [ ] Monitoring service monitoring
- [ ] API rate limiting
- [ ] Redis caching layer
- [ ] Elasticsearch for logging
- [ ] Kubernetes deployment
- [ ] CI/CD pipeline
- [ ] Unit and integration tests
- [ ] Metrics and monitoring (Prometheus/Grafana)

---

## Troubleshooting

### Common Issues

**1. Frontend can't connect to backend**
- Check if all Docker containers are running: `docker ps`
- Verify Traefik is routing correctly: http://localhost:8080
- Check browser console for CORS errors

**2. Database connection errors**
- Ensure databases are running: `docker-compose ps`
- Check environment variables in docker-compose.yml
- Wait for databases to fully initialize (~30 seconds)

**3. JWT token validation fails**
- Check if token is being sent in Authorization header
- Verify token hasn't expired
- Check auth-service logs: `docker logs auth-service`

**4. Port conflicts**
- Ensure ports 80, 3000, 5433, 5434, 5435, 8080 are not in use
- Stop any existing services using these ports

---

## Development Notes

- **Database Strategy**: Using `spring.jpa.hibernate.ddl-auto=update` for automatic schema updates
- **CORS**: Configured for localhost:3000 development
- **Logging**: Traefik access logs in `/logs` directory
- **Hot Reload**: Frontend has hot reload, backend requires rebuild
- **Service Discovery**: Using Docker network DNS resolution

---

Developed for the Distributed Systems (SD) course laboratory assignments.

