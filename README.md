# Distributed System - Energy Management System

## Project Overview

This project implements a distributed system for managing users, devices, and authentication in an energy management context. The system follows a microservices architecture with four main backend services, a message broker (RabbitMQ), a reverse proxy, separate databases, a device data simulator, and a React frontend.

**Assignment 2** extends the system with:
- **Monitoring Microservice** for processing device data and computing hourly energy consumption
- **Device Data Simulator** for generating synthetic smart meter readings
- **RabbitMQ Message Broker** for asynchronous communication
- **User & Device Synchronization** via message queues
- **Dead Letter Queue (DLQ)** retry mechanism for fault tolerance
- **Energy Consumption Charts** for visualizing historical data

## System Architecture

### Microservices Architecture
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              FRONTEND (React :3000)                              │
└──────────────────────────────────────┬──────────────────────────────────────────┘
                                       │
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                         TRAEFIK REVERSE PROXY (:80)                              │
│                    (API Gateway, Auth Forwarding, CORS)                          │
└───────┬─────────────────┬─────────────────┬─────────────────┬───────────────────┘
        │                 │                 │                 │
        ▼                 ▼                 ▼                 ▼
┌───────────────┐ ┌───────────────┐ ┌───────────────┐ ┌───────────────┐
│ Auth Service  │ │ User Service  │ │Device Service │ │  Monitoring   │
│   (:8082)     │ │   (:8080)     │ │   (:8081)     │ │   Service     │
│               │ │               │ │               │ │   (:8083)     │
└───────┬───────┘ └───────┬───────┘ └───────┬───────┘ └───────┬───────┘
        │                 │                 │                 │
        ▼                 ▼                 ▼                 ▼
┌───────────────┐ ┌───────────────┐ ┌───────────────┐ ┌───────────────┐
│   PostgreSQL  │ │   PostgreSQL  │ │   PostgreSQL  │ │   PostgreSQL  │
│   (authdb)    │ │  (example-db) │ │   (devices)   │ │ (monitoringdb)│
│   :5435       │ │   :5433       │ │   :5434       │ │   :5436       │
└───────────────┘ └───────────────┘ └───────────────┘ └───────────────┘

                    ┌─────────────────────────────────┐
                    │        RabbitMQ (:5672)         │
                    │      Management UI (:15672)     │
                    └─────────────────────────────────┘
                              ▲           ▲
                              │           │
              ┌───────────────┘           └───────────────┐
              │                                           │
┌─────────────────────────┐                 ┌─────────────────────────┐
│   Device Data Simulator │                 │    Synchronization      │
│   (Standalone App)      │                 │    Messages (Users,     │
│                         │                 │    Devices)             │
└─────────────────────────┘                 └─────────────────────────┘
```

### Message Queue Architecture (RabbitMQ)
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              RabbitMQ Queues                                     │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                  │
│  USER SYNCHRONIZATION FLOW:                                                      │
│  ┌──────────────┐    ┌─────────────────┐    ┌──────────────────────────────────┐│
│  │ Auth Service │───►│ user.sync.queue │───►│ User Service (spring-demo)       ││
│  │  (Producer)  │    │                 │    │ Creates Person, forwards to      ││
│  └──────────────┘    │  DLQ: user.     │    │ user.to.device.queue             ││
│                      │  sync.dlq       │    └──────────────────────────────────┘│
│                      └─────────────────┘                                         │
│                                              ┌──────────────────────────────────┐│
│                      ┌─────────────────┐    │ Device Service                   ││
│                      │ user.to.device. │───►│ Caches user for device           ││
│                      │ queue           │    │ assignments                      ││
│                      │  DLQ: user.to.  │    └──────────────────────────────────┘│
│                      │  device.dlq     │                                         │
│                      └─────────────────┘                                         │
│                                                                                  │
│  DEVICE SYNCHRONIZATION FLOW:                                                    │
│  ┌──────────────┐    ┌─────────────────┐    ┌──────────────────────────────────┐│
│  │Device Service│───►│device.sync.queue│───►│ Monitoring Service               ││
│  │  (Producer)  │    │                 │    │ Stores device info for           ││
│  └──────────────┘    │  DLQ: device.   │    │ consumption tracking             ││
│                      │  sync.dlq       │    └──────────────────────────────────┘│
│                      └─────────────────┘                                         │
│                                                                                  │
│  DEVICE DATA FLOW:                                                               │
│  ┌──────────────┐    ┌─────────────────┐    ┌──────────────────────────────────┐│
│  │ Data         │───►│device.data.queue│───►│ Monitoring Service               ││
│  │ Simulator    │    │                 │    │ Aggregates hourly consumption    ││
│  └──────────────┘    └─────────────────┘    └──────────────────────────────────┘│
│                                                                                  │
└─────────────────────────────────────────────────────────────────────────────────┘
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

### 4. Monitoring Service (Port 8083) - NEW in Assignment 2
**Location:** `monitoring-service/`

**Technologies:**
- Spring Boot
- Spring Data JPA
- Spring AMQP (RabbitMQ)
- PostgreSQL
- Docker

**Key Features:**
- Consumes device measurements from RabbitMQ queue
- Aggregates measurements into hourly energy consumption
- Stores hourly consumption data in dedicated database
- Provides REST API for historical consumption data
- Device synchronization from Device Service
- Dead Letter Queue (DLQ) retry mechanism

**Main Components:**
- `MeasurementConsumer`: Listens to device.data.queue for sensor data
- `DeviceSyncConsumer`: Listens to device.sync.queue for device CRUD events
- `AggregationService`: Computes hourly energy totals
- `ConsumptionService`: Provides consumption data queries
- `ConsumptionController`: REST endpoints for consumption data
- `HourlyConsumption` entity: Stores aggregated hourly data
- `Device` entity: Local device cache

**Endpoints:**
- `GET /api/monitoring/consumption/{deviceId}?date=YYYY-MM-DD` - Get hourly consumption for a device
- `POST /api/monitoring/consumption/total?date=YYYY-MM-DD` - Get total consumption for multiple devices
- `GET /api/monitoring/health` - Health check

**Database:** PostgreSQL (`db-monitoring`, Port 5436)

---

### 5. Device Data Simulator - NEW in Assignment 2
**Location:** `data-simulator/`

**Technologies:**
- Spring Boot
- Spring AMQP (RabbitMQ)
- Jackson JSON

**Key Features:**
- Generates synthetic smart meter readings every 10 minutes (configurable per device)
- Supports multiple devices via `devices.json` configuration
- Simulates realistic consumption patterns (lower at night, higher in evening)
- Sends JSON messages to RabbitMQ queue

**Configuration File:** `src/main/resources/devices.json`
```json
[
  { "deviceId": "550e8400-e29b-41d4-a716-446655440001", "intervalMillis": 10000 },
  { "deviceId": "550e8400-e29b-41d4-a716-446655440002", "intervalMillis": 15000 }
]
```

**Message Format:**
```json
{
  "timestamp": "2025-12-02T10:30:00Z",
  "deviceId": "550e8400-e29b-41d4-a716-446655440001",
  "measurementValue": 0.45
}
```

---

### 6. RabbitMQ Message Broker - NEW in Assignment 2
**Ports:** 5672 (AMQP), 15672 (Management UI)

**Queues:**
| Queue Name | Purpose | Producer | Consumer |
|------------|---------|----------|----------|
| `user.sync.queue` | User creation/deletion sync | Auth Service | User Service |
| `user.to.device.queue` | Forward user to device service | User Service | Device Service |
| `device.sync.queue` | Device creation/update/deletion | Device Service | Monitoring Service |
| `device.data.queue` | Sensor measurements | Data Simulator | Monitoring Service |

**Dead Letter Queues (DLQ) for Retry:**
| DLQ Name | Original Queue | TTL | Max Retries |
|----------|----------------|-----|-------------|
| `user.sync.dlq` | `user.sync.queue` | 30s | 3 |
| `user.to.device.dlq` | `user.to.device.queue` | 30s | 3 |
| `device.sync.dlq` | `device.sync.queue` | 30s | 3 |

**Management UI:** http://localhost:15672 (guest/guest)

---

### 7. Traefik Reverse Proxy (Port 80, Dashboard: 8080)
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
/auth/*              → auth-service:8082
/api/users/*         → spring-demo:8080/people/*      (with auth)
/api/devices/*       → device-service:8081/devices/*  (with auth)
/api/monitoring/*    → monitoring-service:8083        (with auth)
```

**Middlewares:**
- **CORS**: Enables cross-origin requests from frontend
- **Auth Forward**: Validates JWT tokens with auth-service
- **Path Rewrite**: Transforms external URLs to internal service paths

---

### 8. Frontend (React - Port 3000)
**Location:** `frontend/`



**Key Features:**
- JWT-based authentication
- Role-based UI (ADMIN vs CLIENT views)
- User management (ADMIN only)
- Device management (ADMIN only)
- Device assignment to users (ADMIN only)
- Client device view (CLIENT role)
- **Energy consumption charts** (line/bar charts) - NEW
- **Total consumption summary** for all user devices - NEW
- Responsive navigation
- Token storage and management

**Pages:**
- `LoginPage`: User authentication
- `UsersPage`: User management (ADMIN)
- `DevicesPage`: Device management (ADMIN)
- `AssignDevicePage`: Assign devices to users (ADMIN)
- `ClientDevicesPage`: View assigned devices (CLIENT)
- `EnergyConsumptionPage`: Historical energy consumption charts (CLIENT) - NEW

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
- `consumption` (Double)

**Table: user_and_device**
- `id` (UUID, Primary Key)
- `user_id` (UUID)
- `device_id` (UUID, Foreign Key → device)

**Table: user_cache**
- `id` (UUID, Primary Key)
- `user_id` (UUID)
- `name` (String)

### 4. Monitoring Database (`monitoringdb`) - NEW
**Table: devices**
- `device_id` (UUID, Primary Key)
- `name` (String)
- `max_consumption` (Double)

**Table: hourly_consumption**
- `id` (UUID, Primary Key)
- `device_id` (UUID, Foreign Key → devices)
- `hour_start` (Timestamp)
- `total_kwh` (Double)

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
- Monitoring Service - NEW
- RabbitMQ Message Broker - NEW
- 4 PostgreSQL databases

#### 2. Start Data Simulator (Optional - for testing)
```powershell
cd data-simulator
./mvnw spring-boot:run
```

#### 3. Start Frontend
```powershell
cd frontend
npm install
npm start
```

Frontend will be available at: `http://localhost:3000`

#### 4. Access Points
- **Frontend**: http://localhost:3000
- **Traefik Dashboard**: http://localhost:8080
- **API Gateway**: http://localhost:80
- **RabbitMQ Management**: http://localhost:15672 (guest/guest) - NEW
- **Auth Service** (internal): http://localhost:8082
- **User Service** (internal): http://localhost:8080
- **Device Service** (internal): http://localhost:8081
- **Monitoring Service** (internal): http://localhost:8083 - NEW

### API Documentation
- **Auth Service Swagger**: http://localhost:8082/swagger-ui/index.html
- **User Service Swagger**: http://localhost:8080/swagger-ui/index.html
- **Device Service Swagger**: http://localhost:8081/swagger-ui/index.html
- **Monitoring Service Swagger**: http://localhost:8083/swagger-ui/index.html - NEW

### Database Ports (for direct access)
- Auth DB: `localhost:5435`
- Users DB: `localhost:5433`
- Devices DB: `localhost:5434`
- Monitoring DB: `localhost:5436` - NEW

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

### 5. Energy Consumption Data (Client) - NEW
```
Frontend → GET /api/monitoring/consumption/{deviceId}?date=2025-12-02
       ↓
Traefik → Validate JWT → Route
       ↓
Monitoring Service → Query hourly_consumption table
       ↓
Return hourly consumption data for chart
```

### 6. Asynchronous User Synchronization - NEW
```
Auth Service → Register new user
       ↓
Publish to user.sync.queue
       ↓
User Service → Create Person entity
       ↓
Publish to user.to.device.queue
       ↓
Device Service → Cache user for assignments
```

### 7. Asynchronous Device Synchronization - NEW
```
Device Service → Create/Update/Delete device
       ↓
Publish to device.sync.queue
       ↓
Monitoring Service → Sync device in local DB
```

### 8. Device Data Flow - NEW
```
Data Simulator → Generate measurement every 10 min
       ↓
Publish to device.data.queue
       ↓
Monitoring Service → Aggregate into hourly totals
       ↓
Store in hourly_consumption table
```

---

## Key Features Implemented

### Assignment 1 Features

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

### Assignment 2 Features (NEW)

### Monitoring Microservice
- ✅ Message consumer component (MeasurementConsumer)
- ✅ Hourly energy consumption aggregation
- ✅ Device data storage in dedicated database
- ✅ REST API for consumption queries
- ✅ Device synchronization consumer

### Device Data Simulator
- ✅ Standalone Spring Boot application
- ✅ Configurable device IDs via devices.json
- ✅ Configurable intervals per device
- ✅ Realistic consumption patterns (time-of-day based)
- ✅ JSON message format with timestamp, deviceId, measurementValue

### Message Broker (RabbitMQ)
- ✅ User synchronization queue (user.sync.queue)
- ✅ User-to-device queue (user.to.device.queue)
- ✅ Device synchronization queue (device.sync.queue)
- ✅ Device data queue (device.data.queue)
- ✅ Dead Letter Queues for retry mechanism
- ✅ Automatic retry after 30 seconds (max 3 retries)

### User Synchronization (2 points)
- ✅ Auth Service publishes to user.sync.queue on registration
- ✅ User Service consumes and creates Person entity
- ✅ User Service forwards to user.to.device.queue
- ✅ Device Service caches user for assignments

### Device Synchronization (1 point)
- ✅ Device Service publishes to device.sync.queue on CRUD operations
- ✅ Monitoring Service consumes and syncs device locally
- ✅ Supports CREATE, UPDATE, DELETE operations

### Energy Consumption Charts (2 points)
- ✅ Line chart visualization
- ✅ Bar chart visualization
- ✅ Date picker (calendar) for day selection
- ✅ OX axis: Hours (0-23)
- ✅ OY axis: Energy value in kWh
- ✅ Total daily consumption summary
- ✅ Total consumption for all user devices

### Fault Tolerance
- ✅ Dead Letter Queue (DLQ) retry mechanism
- ✅ Automatic message re-queue after 30 seconds
- ✅ Maximum 3 retry attempts
- ✅ Graceful error handling and logging

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

**Monitoring Service:** - NEW
```env
DB_IP=db-monitoring
DB_PORT=5432
DB_DBNAME=monitoringdb
DB_USER=postgres
DB_PASSWORD=1163
SERVER_PORT=8083
RABBITMQ_HOST=rabbitmq
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest
```

**Data Simulator:** - NEW
```env
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest
QUEUE_NAME=device.data.queue
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
7. View energy consumption charts - NEW
8. Check total consumption summary - NEW

---

## Assignment 2 Specific Testing - NEW

### Test User Synchronization
1. Register a new user via Auth Service
2. Check RabbitMQ Management UI for messages in user.sync.queue
3. Verify user appears in User Service database
4. Verify user appears in Device Service user_cache table

### Test Device Synchronization
1. Create a device via Device Service
2. Check RabbitMQ Management UI for messages in device.sync.queue
3. Verify device appears in Monitoring Service database

### Test Data Simulator
1. Configure devices.json with device IDs
2. Run the data-simulator application
3. Check RabbitMQ Management UI for messages in device.data.queue
4. Verify hourly_consumption table in Monitoring database

### Test Energy Consumption Charts
1. Login as CLIENT
2. Navigate to Energy Consumption page
3. Select a device and date
4. Verify bar/line chart displays correctly
5. Check total consumption summary box

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
- **Message Broker**: RabbitMQ 3-management - NEW
- **Container Orchestration**: Docker Compose
- **Database**: PostgreSQL (4 instances)
- **Networking**: Docker networks
- **API Documentation**: Swagger/OpenAPI 3.0

---

## Assignment 2 Grading Checklist

| Points | Requirement | Status |
|--------|-------------|--------|
| **5p** | **Minimum to pass** | |
| | Monitoring Microservice with message consumer | ✅ |
| | Message producer (Data Simulator) | ✅ |
| | Message broker (RabbitMQ) | ✅ |
| | Insert hourly energy consumption in database | ✅ |
| | Readme file | ✅ |
| **2p** | User Synchronization | ✅ |
| **1p** | Device Synchronization | ✅ |
| **2p** | Historical energy consumption charts | ✅ |
| **10p** | **Project Requirements** | |
| | Reverse Proxy (Traefik) | ✅ |
| | Docker deployment | ✅ |
| | Deployment diagram | ⚠️ (see diagram above) |
| | Device config file for simulator | ✅ (devices.json) |

---

## Project Structure

```
lab/
├── auth-service/              # Authentication microservice
│   └── auth-service/
│       ├── src/main/java/
│       │   └── com/example/auth_service/
│       │       ├── config/
│       │       │   └── RabbitMQConfig.java      # NEW - Queue config
│       │       ├── dto/
│       │       │   └── UserSyncMessage.java     # NEW - Sync DTO
│       │       └── service/
│       │           └── AuthService.java         # Publishes to queue
│       ├── Dockerfile
│       └── pom.xml
├── device-service/            # Device management microservice
│   ├── src/main/java/
│   │   └── com/example/device_service/
│   │       ├── config/
│   │       │   └── RabbitMQConfig.java          # NEW
│   │       ├── dto/
│   │       │   ├── DeviceSyncMessage.java       # NEW
│   │       │   └── UserToDeviceMessage.java     # NEW
│   │       └── service/
│   │           ├── UserSyncConsumer.java        # NEW - Consumes user sync
│   │           └── DeviceService.java           # Publishes device sync
│   ├── Dockerfile
│   └── pom.xml
├── ds2025_spring_example/     # User management microservice
│   └── demo/
│       ├── src/main/java/
│       │   └── com/example/demo/
│       │       ├── config/
│       │       │   └── RabbitMQConfig.java      # NEW
│       │       ├── dto/
│       │       │   └── UserSyncMessage.java     # NEW
│       │       └── service/
│       │           └── UserSyncConsumer.java    # NEW - Consumes & forwards
│       ├── Dockerfile
│       └── pom.xml
├── monitoring-service/        # NEW - Monitoring microservice
│   ├── src/main/java/
│   │   └── com/example/monitoring_service/
│   │       ├── config/
│   │       │   ├── RabbitConfig.java
│   │       │   └── JacksonConfig.java
│   │       ├── controller/
│   │       │   └── ConsumptionController.java
│   │       ├── dto/
│   │       │   ├── MeasurementMessage.java
│   │       │   ├── DeviceSyncMessage.java
│   │       │   ├── DailyConsumptionResponse.java
│   │       │   ├── MultiDeviceRequest.java
│   │       │   └── UserTotalConsumptionResponse.java
│   │       ├── entity/
│   │       │   ├── Device.java
│   │       │   └── HourlyConsumption.java
│   │       ├── repository/
│   │       │   ├── DeviceRepository.java
│   │       │   └── HourlyConsumptionRepository.java
│   │       └── service/
│   │           ├── MeasurementConsumer.java
│   │           ├── DeviceSyncConsumer.java
│   │           ├── AggregationService.java
│   │           └── ConsumptionService.java
│   ├── Dockerfile
│   └── pom.xml
├── data-simulator/            # NEW - Device data simulator
│   ├── src/main/java/
│   │   └── com/example/data_simulator/
│   │       ├── configuration/
│   │       │   ├── DeviceConfig.java
│   │       │   └── SimulatorProperties.java
│   │       └── simulator/
│   │           └── DeviceDataSender.java
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── devices.json               # Device configuration
│   └── pom.xml
├── frontend/                  # React frontend
│   ├── src/
│   │   ├── api.js                     # Added monitoring API
│   │   ├── App.js                     # Added energy page route
│   │   └── pages/
│   │       └── EnergyConsumptionPage.js  # NEW - Charts page
│   ├── public/
│   └── package.json
├── dynamic/                   # Traefik dynamic config
│   └── path.yml               # Added monitoring route
├── logs/                      # Traefik access logs
├── docker-compose.yml         # Added RabbitMQ, monitoring-service
├── traefik.yml               # Traefik static config
└── README.md                 # This file
```

---

## Future Enhancements

### Completed in Assignment 2
- [x] Real-time device monitoring (via data simulator)
- [x] Message queue (RabbitMQ) for async communication
- [x] Monitoring service for energy data
- [x] Dead Letter Queue retry mechanism

### Potential Improvements
- [ ] WebSocket support for live updates
- [ ] API rate limiting
- [ ] Redis caching layer
- [ ] Elasticsearch for logging
- [ ] Kubernetes deployment
- [ ] CI/CD pipeline
- [ ] Unit and integration tests
- [ ] Metrics and monitoring (Prometheus/Grafana)
- [ ] Device alerts when consumption exceeds threshold

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
- Ensure ports 80, 3000, 5433, 5434, 5435, 5436, 8080, 15672 are not in use
- Stop any existing services using these ports

**5. RabbitMQ connection issues** - NEW
- Ensure RabbitMQ is running: `docker logs rabbitmq`
- Check credentials (default: guest/guest)
- Verify port 5672 is accessible
- Check RabbitMQ Management UI: http://localhost:15672

**6. Messages not being consumed** - NEW
- Check if consumer service is running
- Look for DLQ messages in RabbitMQ UI
- Check consumer service logs for errors
- Verify queue names match between producer and consumer

**7. Energy charts not showing data** - NEW
- Ensure data-simulator is running and sending data
- Check if device exists in monitoring database
- Verify date format (YYYY-MM-DD)
- Check monitoring-service logs for aggregation errors

---

## Development Notes

- **Database Strategy**: Using `spring.jpa.hibernate.ddl-auto=update` for automatic schema updates
- **CORS**: Configured for localhost:3000 development
- **Logging**: Traefik access logs in `/logs` directory
- **Hot Reload**: Frontend has hot reload, backend requires rebuild
- **Service Discovery**: Using Docker network DNS resolution

---

Developed for the Distributed Systems (SD) course laboratory assignments.
**Assignment 1**: Microservices Architecture with REST APIs
**Assignment 2**: Asynchronous Communication with RabbitMQ

