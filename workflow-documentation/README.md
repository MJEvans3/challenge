## 1. Executive Summary

This project is a dynamic, rule-based invoice approval workflow system. It was built to solve a challenge that required a workflow engine where the approval logic could be configured without changing the application code.

The solution consists of a Java/Dropwizard backend and a React/Next.js frontend. The core of the solution is a **Rule Engine** that processes an `Invoice` (containing amount, department, and manager approval status) against a list of prioritized rules. It selects the first matching rule to determine the correct approver (e.g., CMO, CFO) and notification channel (e.g., Email, Slack).

This design was chosen because it is **extensible, configurable, and testable**. Instead of hardcoding logic with `if/else` statements, rules are represented as data structures. This allows new conditions or rules to be added without recompiling the code, and it makes the system ready for a database-backed rule management system.

---

## 2. Quick Start & Testing

You can test the application in four ways.

### Option 1: Test via Full-Stack Frontend (Recommended)

This is the easiest way to test the complete workflow system with a visual interface.

**1. Open 2 terminal windows:**

Terminal 1 - Start Backend:
```bash
cd workflow-backend
./gradlew run
```
Note: The server will show `<==========---> 75% EXECUTING`. This is normal - it means the server is running and waiting for requests

Terminal 2 - Start Frontend:
```bash
cd workflow-frontend
npm install  # Only needed the first time
npm run dev
```

**2. Open browser to `http://localhost:3000`**

You'll see a beautiful UI where you can:
- Enter invoice amounts
- Select departments
- Toggle manager approval requirements
- See real-time approval decisions with approver roles and notification channels

The UI displays all 5 workflow rules below the input form, making it easy to understand how decisions are made.

---

### Option 2: Run Unit Tests (Fastest)

This runs the JUnit tests against the workflow engine.

Bash

```
# Make sure you are in the workflow-backend directory
./gradlew test
```

### Option 3: Run Standalone Test Runner

This runs the `WorkflowTestRunner.java` class, which prints the evaluation for all 5 test cases to the console without starting a web server.

Bash

```
# Build the project
./gradlew build

# Run the test runner
./gradlew --console=plain runTestRunner
```

**Expected Output:**

```
===========================================
   Workflow Engine Test Runner
===========================================

=== Evaluating Workflow ===
Invoice: Invoice{amount=15000.0, department='marketing', ...}
Checking Rule: Amount > 10000 AND Marketing Department → CMO...
  ✓ MATCH FOUND!
Sending approval request to CMO via EMAIL...
-------------------------------------------
(and 4 more test cases...)
```

### Option 4: Run Full Server & Test via API

This starts the full Dropwizard server, allowing you to test the `POST /workflow` endpoint.

**1. Start the Server:**

Bash

```
# Make sure you are in the workflow-backend directory
./gradlew run
```

The server will start on `http://localhost:8080`.

**2. Test with `curl` (in a new terminal):**

Bash

```
# Test Case 1: High-value marketing invoice → CMO
curl -X POST http://localhost:8080/workflow \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 15000,
    "department": "marketing",
    "requiresManagerApproval": false
  }'

# Expected Response:
# {"success":true,"approverRole":"CMO","channel":"EMAIL","message":"High value marketing invoice..."}


# Test Case 5: Low-value invoice → Finance Team
curl -X POST http://localhost:8080/workflow \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 3000,
    "department": "hr",
    "requiresManagerApproval": false
  }'

# Expected Response:
# {"success":true,"approverRole":"FINANCE_TEAM","channel":"SLACK","message":"Standard invoice approval..."}
```

---

## 3. Design Decisions & Architecture

This solution is built on several key design patterns and principles.

### Core Pattern: Rule Engine

The primary design is a **Rule Engine**.

- **Why:** The challenge required a dynamic system. A rule engine pattern abstracts the business logic from the main application. Rules become _data_ (stored in `WorkflowRepository`) instead of _code_.
    
- **How:** `WorkflowEngine.java` iterates a list of `WorkflowRule` objects sorted by priority. It stops at the _first rule_ that matches the incoming `Invoice`.
    

### Condition Framework: Strategy Pattern

- **Why:** To make the rule-matching logic extensible. We needed a way to add new types of conditions (e.g., "Amount > 10000", "Department == 'marketing'") without modifying the engine.
    
- **How:** We created a `Condition.java` interface with a single `evaluate(Invoice invoice)` method. Concrete classes like `AmountCondition`, `DepartmentCondition`, and `ManagerApprovalCondition` implement this interface. A `WorkflowRule` simply holds a `List<Condition>` and checks if _all_ of them evaluate to `true`.
    

### Data Layer: In-Memory Repository

- **Why:** The challenge allowed for an in-memory solution but required a design that _could_ be persisted.
    
- **How:** `WorkflowRepository.java` acts as an in-memory database. It initializes the 5 rules from the diagram in its constructor. This class can be easily swapped with a new implementation (e.g., `PostgresWorkflowRepository`) that reads from a database, without changing the `WorkflowEngine` that uses it.
    

### API Layer: DTOs & Decoupling

- **Why:** To create a clean, formal contract for the API and to prevent internal domain models (like `Invoice`) from being exposed directly.
    
- **How:** `WorkflowResource.java` (the REST endpoint) accepts a `WorkflowRequest` (DTO) and returns a `WorkflowResponse` (DTO). These DTOs are simple data-transfer objects, while the `Invoice` object contains the actual business logic.
    

### SOLID Principles

The design adheres to SOLID principles:

- **S**ingle Responsibility: `WorkflowEngine` evaluates rules, `WorkflowRepository` stores rules, `Condition` classes check one logic block.
    
- **O**pen/Closed: The `WorkflowEngine` is _closed_ for modification but _open_ for extension. You can add new `WorkflowRule` objects or new `Condition` types without changing the engine itself.
    
- **L**iskov Substitution: Any `Condition` implementation can be used in the `WorkflowRule`'s list.
    
- **I**nterface Segregation: The `Condition` interface is small and focused.
    
- **D**ependency Inversion: The `WorkflowEngine` depends on the `WorkflowRepository` abstraction, not a concrete implementation.
    

---

## 4. Use of AI in Development

This project was developed with the assistance of AI tools, which were used strategically to enhance productivity while maintaining full ownership of the design and implementation decisions.

### AI Usage Overview

**Architecture & Planning:**
- Brainstorming different architectural approaches before starting implementation
- Visualizing and refining the workflow architecture diagrams
- Discussing trade-offs between different design patterns (e.g., Strategy vs. Chain of Responsibility)

**Code Generation & Documentation:**
- Native code completion for boilerplate and repetitive code
- Generating comprehensive JavaDoc comments and inline documentation
- Writing design rationale comments explaining benefits of chosen approaches
- Drafting "Future Enhancements" sections in code documentation

**Database Design:**
- Creating visual database schema diagrams after implementing table definitions
- Validating database normalization and relationship design

**Testing & Debugging:**
- Debugging Gradle configuration issues on a new development machine
- Testing edge cases and generating comprehensive test scenarios
- Troubleshooting build system compatibility problems

**Language Conversion:**
- Converting starter Kotlin files from the original challenge template to Java
- Ensuring idiomatic Java patterns were followed in the conversion

AI was used as a productivity multiplier and debugging assistant, not as a replacement for software engineering judgment. I reviewed every line of code, understood it, and approved by me before being committed.

---

## 5. API Reference

### `POST /workflow`

Evaluates an invoice and returns the required approval action.

**Request Body:**

JSON

```
{
  "amount": 15000.00,
  "department": "marketing",
  "requiresManagerApproval": false
}
```

**Success Response (200 OK):**

JSON

```
{
  "success": true,
  "approverRole": "CMO",
  "channel": "EMAIL",
  "message": "High value marketing invoice requires CMO approval"
}
```

**Error Response (404 Not Found - No Rule Matched):**

JSON

```
{
  "success": false,
  "error": "No matching workflow rule found for invoice: ..."
}
```

**Error Response (400 Bad Request - Invalid Input):**

JSON

```
{
  "success": false,
  "error": "Department is required"
}
```

---

## 6. Core Technical Artifacts

- **Architecture Diagrams:** See `ARCHITECTURE_DIAGRAMS.md` for request flow, class relationships, and workflow logic diagrams.
    
- **Database Schema:** See `DATABASE_SCHEMA.md` for the complete production-ready database design that this in-memory solution is based on.
    

---

