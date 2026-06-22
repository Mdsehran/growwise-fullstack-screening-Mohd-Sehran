# GrowWise Webhook Delivery Log Dashboard

Full Stack Screening Assignment

**Candidate:** Mohd Sehran

## Project Goal

This assignment simulates an internal integrations dashboard used by administrators to monitor webhook delivery attempts.

The system provides visibility into webhook activity, supports operational troubleshooting through retries, and enforces server-side authorization so that only administrators can access integration logs.

The implementation intentionally stays within the requested scope and focuses on correctness, authorization, usability, testing, and maintainability.

---

# Assignment Requirements Mapping

| Requirement            | Status |
| ---------------------- | ------ |
| Spring Boot 3 Backend  | ✅      |
| Java                   | ✅      |
| REST API               | ✅      |
| JPA / Hibernate        | ✅      |
| H2 Database            | ✅      |
| Next.js App Router     | ✅      |
| TypeScript             | ✅      |
| Simulated Auth Headers | ✅      |
| Admin Authorization    | ✅      |
| Status Filtering       | ✅      |
| Pagination             | ✅      |
| Retry Workflow         | ✅      |
| Seed Data              | ✅      |
| Backend Tests          | ✅      |
| Frontend Test          | ✅      |
| Loading State          | ✅      |
| Empty State            | ✅      |
| Error State            | ✅      |
| Submit State           | ✅      |

---

# Problem Being Solved

Webhook integrations are commonly used to notify external systems when events occur inside an application.

Example events:

* QUESTION_ASKED
* QUESTION_ANSWERED
* GRADE_PUBLISHED

Every delivery attempt generates a record.

A delivery can be:

* SUCCESS
* FAILED
* PENDING

Administrators need a way to:

1. View delivery history
2. Filter by status
3. Investigate failures
4. Retry failed deliveries
5. Preserve audit history

---

# Solution Overview

The solution consists of:

## Backend

Spring Boot application responsible for:

* Authorization enforcement
* Delivery attempt retrieval
* Filtering
* Pagination
* Retry workflow
* Seed data generation

## Frontend

Next.js dashboard responsible for:

* Viewing webhook attempts
* Switching simulated user roles
* Filtering by status
* Pagination controls
* Retry interactions
* Error handling and user feedback

---

# Architecture

## High Level Flow

```text
Next.js Dashboard
        |
        v
AdminAuthInterceptor
        |
        v
WebhookAdminController
        |
        v
JPA Repository
        |
        v
H2 Database
```

The backend performs authorization before any webhook data can be accessed.

Only requests containing:

```http
X-Role: ADMIN
```

are permitted.

All authorization decisions are enforced server-side.

---

# API Endpoints

## Retrieve Webhook Attempts

```http
GET /api/admin/webhook-attempts
```

Supports:

```http
GET /api/admin/webhook-attempts?status=FAILED&page=0&size=10
```

### Query Parameters

| Parameter | Description                |
| --------- | -------------------------- |
| status    | SUCCESS / FAILED / PENDING |
| page      | Page number                |
| size      | Page size                  |

---

## Retry Failed Delivery

```http
POST /api/admin/webhook-attempts/{attemptId}/retry
```

Rules:

* Admin only
* Attempt must exist
* Attempt must be FAILED
* Creates a new PENDING attempt
* Original record remains unchanged

---

# Authorization Model

The assignment required simulated authentication using request headers.

Supported roles:

| Role       | Access    |
| ---------- | --------- |
| ADMIN      | Allowed   |
| INSTRUCTOR | Forbidden |
| STUDENT    | Forbidden |

Examples:

### Admin

```http
X-Role: ADMIN
```

Result:

```http
200 OK
```

### Instructor

```http
X-Role: INSTRUCTOR
```

Result:

```http
403 Forbidden
```

### Student

```http
X-Role: STUDENT
```

Result:

```http
403 Forbidden
```

---

# Seed Data

The application starts with:

* 1 Admin
* 1 Instructor
* 1 Student
* 2 Webhook Subscriptions
* 8 Delivery Attempts

Statuses include:

* SUCCESS
* FAILED
* PENDING

This allows immediate evaluation without additional setup.

---

# Key Design Decision

## Why Create New Retry Attempts?

Instead of modifying an existing FAILED record:

```text
att-0004 FAILED
```

Retry creates:

```text
att-0004 FAILED
new-attempt-id PENDING
```

Benefits:

* Preserves audit history
* Makes retries traceable
* Reflects real-world webhook systems
* Avoids mutation of historical records

---

# User Interface Validation

## Administrator View

<img width="959" alt="Admin Dashboard" src="https://github.com/user-attachments/assets/af489952-df67-40c8-b973-43d7a70aaaef">


Demonstrates:

* Authorized access
* Paginated results
* Filtering
* Retry functionality
* Operational visibility

---

## Instructor View

<img width="959" height="520" alt="Screenshot 2026-06-21 132241" src="https://github.com/user-attachments/assets/45f05e6b-9c7d-419c-9871-c1d22f146007" />


Demonstrates:

* Authorization enforcement
* Restricted operational access

---

## Student View

<img width="956" height="518" alt="Screenshot 2026-06-21 132316" src="https://github.com/user-attachments/assets/c4ae0485-3749-45b1-ac87-3c9dfb726b43" />


Demonstrates:

* Consistent role-based restrictions
* Protection of integration logs

---

# Sequence Diagram

<img width="655" height="439" alt="image" src="https://github.com/user-attachments/assets/7ce465e5-8cf0-4d64-ad0e-31ad4879ce0c" />


The sequence diagram illustrates:

1. Authorization checks
2. Delivery attempt retrieval
3. Retry workflow
4. Error handling paths
5. Audit-preserving retry behavior

---

# Verification

The implementation was validated at three levels.

## 1. User Interface Testing

Verified:

* Dashboard rendering
* Filters
* Pagination
* Retry actions
* Error handling
* Authorization states

---

## 2. API Testing

Verified using curl:

### Unauthorized Request

```http
403 Forbidden
```

### Admin Request

```http
200 OK
```

### Retry Success

```http
201 Created
```

### Invalid Retry

```http
404 Not Found
```

---

## 3. Automated Tests

Executed:

```bash
mvn test
```

Result:

```text
Tests run: 7
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```
<img width="720" height="155" alt="Screenshot 2026-06-21 135329" src="https://github.com/user-attachments/assets/c6d81562-8856-4594-b662-cfe72bde2e7f" />

Coverage includes:

* Authorization behavior
* Retry workflow
* Integration scenarios

---

# Running the Project

## Prerequisites

* Java 17+
* Maven 3.9+
* Node.js 18+

Verify installation:

```bash
java -version
mvn -version
node -v
npm -v
```

---

## Backend

```bash
cd backend
mvn spring-boot:run
```

Backend:

```text
http://localhost:8080
```

---

## Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend:

```text
http://localhost:3000
```

---

# Running Tests

Backend:

```bash
cd backend
mvn test
```

Frontend:

```bash
cd frontend
npm test
```

---

# Trade-Offs

Given the assignment time-box (4–6 hours), several intentional trade-offs were made:

* Simulated authentication via headers instead of OAuth/SSO
* H2 in-memory database instead of external infrastructure
* No deployment pipeline
* No external webhook execution
* H2 console enabled for local development only
* No idempotency protection on retry endpoint
* Retried attempts remain PENDING indefinitely
* CORS allowlist configured for localhost development

These trade-offs align with the assignment scope while keeping the implementation focused and maintainable.

---

# Conclusion

This project implements a complete webhook delivery monitoring workflow with:

* Secure admin-only access
* Delivery visibility
* Status filtering
* Pagination
* Retry functionality
* Seeded demonstration data
* Automated tests
* Frontend and backend integration

The solution was validated through UI testing, API testing, and automated test execution, with all core assignment requirements successfully implemented.
