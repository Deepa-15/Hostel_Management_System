# 🏠 Hostel Management System

A comprehensive **Hostel Management System** built using **Object-Oriented Analysis and Design (OOAD)** principles. The system automates hostel operations including student registration, room allocation, fee payment, complaint tracking, and vacating — replacing manual processes with a robust, role-based web application.

---

## 📌 Problem Statement

Manual hostel management is error-prone and time-consuming. This system automates:
- Student registration and hostel room applications
- Warden-based room allocation approval/rejection
- Fee payment processing with multiple payment methods
- Complaint lifecycle management (submit → in-progress → resolved → closed)
- Real-time room availability tracking with double-booking prevention

---

## 🛠️ Technology Stack

| Layer | Technology |
|-------|-----------|
| **Backend** | Java 17+ |
| **Framework** | Spring Boot 3.2.5 (MVC) |
| **ORM** | Hibernate / Spring Data JPA |
| **Database** | MySQL (production) / H2 (development) |
| **Frontend** | Thymeleaf + HTML/CSS |
| **Security** | Spring Security (Role-based access) |
| **Build Tool** | Maven (with Maven Wrapper) |

---

## 👥 System Actors

| Actor | Capabilities |
|-------|-------------|
| **Student** | Register, login, browse rooms, apply for room, pay fees, file complaints, vacate |
| **Warden** | Approve/reject allocations, manage complaints, view room occupancy |
| **Admin** | Manage hostels, rooms, users; view all allocations, payments, and complaints |

---

## ✅ Core Features

### Major Use Cases
- ✅ Student Registration & Login (role-based)
- ✅ Apply for Hostel Room
- ✅ Room Allocation (Warden approval workflow)
- ✅ Fee Payment System (Cash / Online / Bank Transfer)
- ✅ Complaint Management System (lifecycle tracking)
- ✅ Vacating Hostel

### Minor Use Cases
- ✅ View Room Availability (real-time)
- ✅ Track Complaint Status
- ✅ Payment History
- ✅ Admin managing rooms/students/hostels

### Business Rules
- 🔒 **Double-booking prevention** — one active allocation per student
- 💰 **Payment before confirmation** — allocation confirmed only after fee payment
- 🔄 **Complaint lifecycle** — validated status transitions (SUBMITTED → IN_PROGRESS → RESOLVED → CLOSED)
- 🛡️ **Role-based access control** — separate dashboards per role

---

## 🏗️ Design Principles (OOAD — SOLID)

| Principle | Where Applied |
|-----------|---------------|
| **SRP** (Single Responsibility) | Each service/controller handles one domain. `User.java` stores only identity; allocation logic is in `AllocationService`. |
| **OCP** (Open/Closed) | `PaymentStrategy` interface — new payment methods added without modifying existing code. `UserFactory` — new roles extensible without changes. |
| **LSP** (Liskov Substitution) | All `PaymentStrategy` implementations are interchangeable. `User` entity works regardless of role. |
| **DIP** (Dependency Inversion) | Controllers depend on service *interfaces*, not implementations. `ComplaintEventPublisher` depends on `ComplaintObserver` abstraction. |

---

## 🎨 Design Patterns

| Pattern | Type | Where Used | Why |
|---------|------|-----------|-----|
| **Factory** | Creational | `UserFactory.java` — creates role-specific User objects | Centralizes user creation logic; extensible for new roles |
| **Facade** | Structural | `HostelManagementFacade.java` — unified interface to Room, Allocation, Payment, Complaint services | Simplifies controller-service interaction |
| **Observer** | Behavioral | `ComplaintObserver` + `ComplaintEventPublisher` + concrete observers | Decouples complaint status changes from notification logic |
| **Strategy** | Behavioral | `PaymentStrategy` + `CashPaymentStrategy` / `OnlinePaymentStrategy` / `BankTransferPaymentStrategy` | Different payment methods processed interchangeably at runtime |
| **Singleton** | Framework | All Spring `@Service`, `@Component`, `@Repository` beans | Spring IoC manages single instances by default |

---

## 🗃️ Database Design

### Entity-Relationship Overview

```
USERS ──┬── HOSTELS (warden_id FK)
        ├── ALLOCATIONS (student_id FK, approved_by FK) ── ROOMS (hostel_id FK)
        ├── PAYMENTS (student_id FK, allocation_id FK)
        └── COMPLAINTS (student_id FK, resolved_by FK)
```

### Tables

| Table | Key Columns | Relationships |
|-------|------------|---------------|
| `users` | id, username, password, fullName, email, phone, role | Base entity for all actors |
| `hostels` | id, name, address, totalRooms, warden_id (FK→users) | Has many rooms |
| `rooms` | id, roomNumber, roomType, capacity, currentOccupancy, feePerSemester, hostel_id (FK→hostels) | Belongs to hostel |
| `allocations` | id, student_id (FK→users), room_id (FK→rooms), status, applicationDate, approved_by (FK→users) | Links student to room |
| `payments` | id, student_id (FK→users), allocation_id (FK→allocations), amount, paymentMethod, transactionId | Tracks fee payments |
| `complaints` | id, student_id (FK→users), title, description, category, status, resolved_by (FK→users) | Lifecycle tracking |

---

## 📁 Project Structure

```
ooad_project/
├── .gitignore
├── README.md
├── pom.xml
├── mvnw.cmd
├── .mvn/wrapper/
│   └── maven-wrapper.properties
│
└── src/main/
    ├── java/com/hostel/
    │   ├── HostelManagementApplication.java
    │   ├── config/           # Security, DataInitializer, UserDetailsService
    │   ├── controller/       # AuthController, StudentController, WardenController, AdminController
    │   ├── model/            # JPA Entities (User, Hostel, Room, Allocation, Payment, Complaint)
    │   │   └── enums/        # Role, AllocationStatus, ComplaintStatus, PaymentStatus, etc.
    │   ├── repository/       # Spring Data JPA repositories
    │   ├── service/          # Service interfaces
    │   │   └── impl/         # Service implementations
    │   ├── pattern/
    │   │   ├── factory/      # UserFactory (Factory Pattern)
    │   │   ├── facade/       # HostelManagementFacade (Facade Pattern)
    │   │   ├── observer/     # ComplaintObserver, EventPublisher (Observer Pattern)
    │   │   └── strategy/     # PaymentStrategy implementations (Strategy Pattern)
    │   └── exception/        # Custom exceptions + GlobalExceptionHandler
    │
    └── resources/
        ├── application.properties
        ├── static/css/style.css
        └── templates/
            ├── login.html, register.html, error.html
            ├── student/      # 7 templates (dashboard, rooms, allocations, payment, etc.)
            ├── warden/       # 4 templates (dashboard, allocations, complaints, rooms)
            └── admin/        # 7 templates (dashboard, users, hostels, rooms, allocations, etc.)
```

---

## 🚀 How to Run

### Prerequisites
- **Java 17+** (tested with Java 24)
- **MySQL** *(optional — H2 in-memory database is used by default)*

### Quick Start (H2 — No MySQL needed)

```bash
# Set JAVA_HOME (adjust path to your Java installation)
set JAVA_HOME=C:\Program Files\Java\jdk-24

# Run with Maven Wrapper
.\mvnw.cmd spring-boot:run
```

Open **http://localhost:8080** in your browser.

### With MySQL

1. Create the database:
   ```sql
   CREATE DATABASE hostel_management;
   ```
2. Edit `src/main/resources/application.properties`:
   - Uncomment the MySQL configuration lines
   - Comment the H2 configuration lines
   - Update username/password
3. Run: `.\mvnw.cmd spring-boot:run`

---

## 🔑 Default Login Credentials

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| Warden | `warden1` | `warden123` |
| Warden | `warden2` | `warden123` |
| Student | `student1` | `student123` |
| Student | `student2` | `student123` |
| Student | `student3` | `student123` |

---

## 📸 Screenshots

### Login Page
Dark-themed login with demo credentials, gradient button, and registration link.

### Student Dashboard
Welcome panel with stat cards (allocations, payments, complaints, room status), sidebar navigation, and quick actions.

### Available Rooms
Room cards showing hostel, type, capacity, available beds, fee per semester, and one-click apply.

---

## 📄 License

This project was developed as an academic mini-project for the **Object-Oriented Analysis and Design (OOAD)** course.
