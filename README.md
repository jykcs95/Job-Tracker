# 📊 Job Application Kanban Tracker

A highly scalable, production-grade distributed system designed to manage job hunt workflows. This application utilizes an enterprise-grade microservice architecture featuring **Event-Driven Streaming**, **In-Memory Caching**, and **Decoupled Analytics Aggregations**.

A full-stack job application workflow tracker built with:

- **React + Vite** frontend
- **Spring Boot** Java microservices
- **MySQL, Redis, Kafka** infrastructure

This repository contains a modern job-search Kanban dashboard with a decoupled workflow service and an analytics worker for event-driven metrics.

---

## Repository Structure

- `job-tracker-ui/` - React/Vite frontend application
- `job-workflow-service/` - core REST service for job card CRUD and Redis caching
- `analytics-service/` - asynchronous analytics worker consuming Kafka events
- `infrastructure/` - Docker Compose stack for local services
- `start_project.bat` - convenience script to launch the full stack on Windows

---

## 🏗️ System Architecture & Data Flow

1. **Write Layer**: React calls the **Job Workflow Service** REST endpoints to save or update applications. Data is permanently committed to **MySQL**.
2. **Performance Layer**: To maximize dashboard speeds, user Kanban boards are cached inside **Redis** as JSON strings. Modifying any job instantly evicts the stale cache key.
3. **Streaming Layer**: Card movements trigger asynchronous telemetry packets streamed into **Apache Kafka** topic queues.
4. **Analytics Layer**: The standalone **Analytics Engine Service** consumes these event streams silently in the background, computing running system-wide daily totals in a dedicated reporting table.

---

## 🛠️ Technology Stack

- Frontend: **React 19**, **Vite**, **Tailwind CSS v4**, **Recharts**
- Backend: **Java 25**, **Spring Boot**, **Spring Data JPA**, **Spring Data Redis**, **Spring for Apache Kafka**
- Database: **MySQL**
- Cache: **Redis**
- Messaging: **Apache Kafka**
- Build tools: **Maven**, **npm**

---

## Getting Started

### Prerequisites

- Docker Desktop (running)
- Java JDK 25
- Node.js (LTS)
- Git

### Quick start

From the repository root, run:

```cmd
start_project.bat
```

This helper script is intended to launch the full stack locally.

### Manual startup

1. Start the infrastructure stack:

```cmd
cd infrastructure
docker compose up -d
```

2. Run the Java services:

```cmd
cd ..\job-workflow-service
mvn clean spring-boot:run
```

In a separate terminal:

```cmd
cd ..\analytics-service
mvn clean spring-boot:run
```

3. Run the frontend:

```cmd
cd ..\job-tracker-ui
npm install
npm run dev
```

4. Open the dashboard at:

```text
http://localhost:5173
```

## 📂 Repository Workspace Structure

```text
Job Tracker/
├── infrastructure/
│   └── docker-compose.yml       # Orchestrates MySQL, Redis, & Kafka containers
├── job-workflow-service/        # Core operational CRUD REST API & Redis caching
│   ├── src/main/java/com/tracker/job_workflow_service/
│   │   ├── config/              # Redis templates & Kafka message producers
│   │   ├── controller/          # REST Controller mappings (/api/jobs)
│   │   ├── dto/                 # Inbound Request payloads & data contracts
│   │   ├── model/               # Relational write entities & workflow enums
│   │   └── service/             # Master transactional business logic
│   └── pom.xml
├── analytics-service/           # Decoupled metrics background processing worker
│   ├── src/main/java/com/tracker/analytics_service/
│   │   ├── config/              # Live active Kafka stream consumer listeners
│   │   ├── dto/                 # Serialized inbound message contracts
│   │   ├── model/               # Operational aggregate snapshot models
│   │   └── repository/          # Metrics analytical database access layers
│   └── pom.xml
├── job-tracker-ui/              # React single page Kanban Dashboard frontend
│   ├── src/
│   │   ├── components/          # Reusable visual presentation frame blocks
│   │   ├── hooks/               # Custom state & asynchronous network hooks
│   │   ├── App.jsx              # Master application assembler panel
│   │   └── index.css            # Tailwind compilation directive base
├── start_project.bat            # One-click desktop automation startup script
└── .gitignore                   # Universal full-stack cache exclusions manifest
```

---

## 🚀 Local Development Environment Quickstart

### Prerequisites

Ensure your local Windows computer has the following tools installed and active:

- **Docker Desktop** (Engine must be running)
- **Java Development Kit (JDK 25)**
- **Node.js (LTS version v22 or higher)**

### 1. Clones the Repository

```cmd
git clone https://github.com
cd "Job Tracker"
```

### 2. Configure System Security Environment Variables

To avoid hardcoding secrets, the databases utilize environment variables. For local development sandbox isolation, safe defaults are pre-baked into the properties. To inject custom tokens into your operating system instance, run:

```cmd
set DB_PASSWORD=your_secure_password
```

### 3. The One-Click Automation Launch (Recommended)

This repository includes a desktop automation batch wrapper script. Double-clicking **`start_project.bat`** executes the entire microservice life-cycle seamlessly:

1. Spins up Docker clusters (`MySQL`, `Redis`, `Kafka`).
2. Compiles and launches the Spring Boot REST Engine on background execution threads.
3. Compiles and serves the React Vite dashboard.
4. Pauses for initialization, then **automatically pops open Google Chrome directly to your running dashboard at `http://localhost:5173`**.

> 🛑 **The Clean Kill Switch**: To safely turn off your backend, frontend, and Docker container databases simultaneously, simply press `Ctrl + C` or click the `X` close button on the main batch script terminal console box.

---

## 📡 API Architecture Specification

All endpoints require a mock authentication identifier passed inside the request metadata headers: `X-User-Id: user123`

### 1. Create a Job Tracking Card

- **URL**: `POST /api/jobs`
- **Payload Request Body (DTO)**:

```json
{
  "companyName": "Netflix",
  "roleTitle": "Senior Backend Engineer",
  "state": "APPLIED",
  "salaryRange": "\$150,000 - \$180,000",
  "jobUrl": "https://netflix.com"
}
```

### 2. Fetch User Kanban Board

- **URL**: `GET /api/jobs`
- **Cache Strategy**: Triggers a `REDIS CACHE HIT` fetching directly out of high-speed memory strings. If a `CACHE MISS` happens, it queries MySQL, populates a Redis cache entry with a 10-minute Time-To-Live (TTL), and passes the dataset back.

### 3. Advance Kanban Status (Triggers Asynchronous Streams)

- **URL**: `PUT /api/jobs/{id}/state?newState=INTERVIEWING`
- **Downstream Events**: Wipes the stale Redis board key cache, commits the new value to MySQL, and drops an immutable status tracker packet onto the Apache Kafka broker cluster topic `job-status-events`.

### 4. Delete Tracking Card

- **URL**: `DELETE /api/jobs/{id}`

## Contact

For issues or improvements, update this README with setup changes or open a dedicated issue in the repository.
