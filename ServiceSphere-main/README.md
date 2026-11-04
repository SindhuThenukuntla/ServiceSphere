# ServiceSphere — Enterprise IT Service Management Platform

ServiceSphere is a production-grade Enterprise IT Service Management (ITSM) web application designed for corporate IT environments to track incident tickets, manage company assets across their lifecycle, enforce warranty and maintenance compliance, and ensure data integrity across departments.

---

## 7. Five-Person Ownership Plan — Student 3 Module

| Student | Primary Ownership | Cross-Team Contribution |
| :--- | :--- | :--- |
| **Student 3 (This Module)** | **Asset inventory, assignment/return, and history** | **Review integrity/transaction rules; validate ticket-asset linking** |
| Student 1 | Authentication, users, roles, department foundation | Security reviews, DTOs & error convention |
| Student 2 | Ticket lifecycle, comments, history, requester experience | Ticket integration tests |
| Student 4 | SLA rules, escalation, notifications and queues | Status transitions & scheduled queues |
| Student 5 | Knowledge base, reports/dashboard and release | Frontend UX & release readiness |

---

## Student 3 Functional Scope & Architecture

### 1. Asset Inventory Management
- **Asset Specifications**: `asset_tag` (UNIQUE), `asset_type`, `brand`, `model`, `serial_number`, `status`, `purchase_date`, `warranty_end_date`, `location`, `notes`.
- **Supported Asset Types**:
  - `Laptop`, `Desktop`, `Monitor`, `Phone`, `Peripheral`, `Network Equipment`
- **Supported Statuses**:
  - `In Stock`, `Assigned`, `In Repair`, `Retired`
- **Warranty & Maintenance Tracking**:
  - Automatically identifies expired warranties and assets expiring within 30 days (`warrantyExpiringSoon`, `daysUntilWarrantyExpiry`).
  - Flags assets due for maintenance/contract renewals with visual alerts in UI and REST API.

### 2. Assignment & Return Lifecycle (Atomic Transactions)
- **Multi-step Business Operations** wrapped in `@Transactional`:
  1. **Assign Asset**:
     - Enforces integrity rule: Assets must be `In Stock` to be assigned.
     - **Integrity Rule**: Rejects assignment if asset is `Retired` or `In Repair` (`400 BAD_REQUEST`).
     - **Active Assignment Rule**: Ensures an asset can have many historical assignments but **no more than one active assignment at a time** (`409 CONFLICT`).
     - Atomically creates an `AssetAssignment` record with `assigned_at`, `condition_out`, and `assigned_by`, and updates the asset status to `Assigned`.
  2. **Return Asset**:
     - Validates active assignment existence.
     - Atomically sets `returned_at` and records `condition_in` inspection notes.
     - Updates asset status to the specified post-return state (`In Stock`, `In Repair`, or `Retired`).
  3. **Assignment History Audit Trail**:
     - Retains chronological history of all past assignments with employee names, dates, condition out, condition in, and staff actors.

### 3. Cross-Team Contribution: Ticket-Asset Linking Validation
- Validates relational integrity when support tickets reference hardware assets (`tickets.asset_id`):
  - **Asset Existence**: Validates `asset_id` foreign key existence on ticket creation/linking.
  - **Status Validation**: Rejects linking `Retired` assets to active operational tickets.
  - **Cross-Querying**: Provides endpoints to view all support tickets linked to an asset (`GET /api/assets/{id}/tickets`).
  - **Atomic Link/Unlink**: Allows support agents and asset managers to link and unlink assets from tickets.

---

## Technology Stack

- **Persistence**: **MySQL 8** (strictly relational, InnoDB, foreign keys, index lookups).
- **Backend**: **Java 21**, **Spring Boot 3.3.4**, Spring Web, Spring Data JPA / Hibernate, Spring Validation, Spring Security, BCrypt.
- **Frontend**: **React 19**, **Vite 8**, Modular CSS, responsive UI.
- **Testing**: JUnit 5, Mockito, Spring Boot Test, H2 (for headless test execution).

---

## Database Design & Relational Model (MySQL 8)

### Tables
1. **`roles`**: `id`, `name UNIQUE`, `description`
2. **`departments`**: `id`, `name UNIQUE`, `manager_user_id`
3. **`users`**: `id`, `full_name`, `email UNIQUE`, `password_hash`, `role_id`, `department_id`, `status`, `created_at`, `updated_at`
4. **`assets`**: `id`, `asset_tag UNIQUE`, `asset_type`, `brand`, `model`, `serial_number`, `status`, `purchase_date`, `warranty_end_date`, `location`, `notes`, `created_at`, `updated_at`
5. **`asset_assignments`**: `id`, `asset_id`, `user_id`, `assigned_at`, `returned_at` (NULL while active), `condition_out`, `condition_in`, `assigned_by`, `created_at`
6. **`tickets`**: `id`, `ticket_number UNIQUE`, `title`, `description`, `category_id`, `priority`, `status`, `requester_id`, `assignee_id`, `department_id`, `asset_id` (NULLABLE), `created_at`, `updated_at`

### Integrity Constraints & Foreign Keys
- `fk_assignment_asset`: `asset_assignments.asset_id` -> `assets.id` (`ON DELETE RESTRICT`)
- `fk_assignment_user`: `asset_assignments.user_id` -> `users.id` (`ON DELETE RESTRICT`)
- `fk_assignment_assigner`: `asset_assignments.assigned_by` -> `users.id` (`ON DELETE RESTRICT`)
- `fk_tickets_asset`: `tickets.asset_id` -> `assets.id` (`ON DELETE SET NULL`)
- Indexes: `idx_assets_tag`, `idx_assets_status`, `idx_assets_type`, `idx_assignment_active (asset_id, returned_at)`, `idx_tickets_asset`.

---

## REST API Specification

### Asset Endpoints

| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/assets` | Paginated asset list with filters (`type`, `status`, `search`) | All authenticated |
| `GET` | `/api/assets/{id}` | Asset details with active assignment & warranty flags | All authenticated |
| `POST` | `/api/assets` | Create new IT asset (validates unique `assetTag`) | IT Asset Manager / Admin |
| `PATCH` | `/api/assets/{id}` | Partial update of asset metadata | IT Asset Manager / Admin |
| `POST` | `/api/assets/{id}/assign` | Assign asset to employee (atomic transaction) | IT Asset Manager / Admin |
| `POST` | `/api/assets/{id}/return` | Return asset with condition notes & next status | IT Asset Manager / Admin |
| `GET` | `/api/assets/{id}/history` | Chronological assignment audit history | IT Asset Manager / Agent |
| `GET` | `/api/assets/user/{userId}` | Get active assets assigned to specific user | Employee / Requester |
| `GET` | `/api/assets/summary` | Totals by status, warranty expiry counts | All roles / Dashboard |
| `GET` | `/api/reports/assets/summary` | Asset summary metrics report | All roles / Dashboard |
| `DELETE` | `/api/assets/{id}` | Soft-decommission / archive asset | Admin |

### Ticket-Asset Linking Endpoints (Cross-Team Contribution)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/assets/{assetId}/tickets` | Get all support tickets filed for a specific asset |
| `POST` | `/api/tickets/{ticketId}/link-asset/{assetId}` | Link asset to ticket (validates retired asset rule) |
| `DELETE` | `/api/tickets/{ticketId}/unlink-asset` | Unlink asset from ticket |
| `POST` | `/api/tickets` | Create ticket with optional validated asset reference |
| `GET` | `/api/tickets` | List tickets with linked asset information |

---

## How to Run the Project

### 1. Prerequisites
- **Java 21 JDK**
- **MySQL 8.0** running on `localhost:3306` with database `servicesphere_db`
- **Node.js 18+** / **npm**

### 2. Configure & Run Backend
1. Ensure MySQL is running on `localhost:3306`:
   ```sql
   CREATE DATABASE IF NOT EXISTS servicesphere_db;
   ```
2. Navigate to backend:
   ```bash
   cd backend/servicesphere-backend/servicesphere-backend
   ```
3. Run with Maven (uses `root` user and configured password):
   ```bash
   mvn spring-boot:run
   ```
   *The backend will start on port `8080`, apply schema updates, and auto-seed demo users, assets, and tickets.*

### 3. Run Backend Automated Tests
Execute the unit and integrity test suite:
```bash
cd backend/servicesphere-backend/servicesphere-backend
mvn test "-Dtest=AssetServiceTest,TicketAssetValidationServiceTest"
```
**Results: 13 / 13 tests pass!**

### 4. Run Frontend
1. Navigate to frontend:
   ```bash
   cd frontend
   ```
2. Start the Vite development server:
   ```bash
   npm run dev
   ```
3. Open `http://localhost:5173` in your browser.

---

## Evaluation & Viva Guide (Student 3)

### Q1: How do you prevent two active assignments for the same asset?
**Answer**:
1. Relational schema constraint: `asset_assignments.returned_at` is `NULL` for an active assignment.
2. Business service layer check: Before creating an assignment, `assetAssignmentRepository.existsByAssetIdAndReturnedAtIsNull(assetId)` verifies no open assignment exists.
3. Status constraint: The asset's status must be `In Stock`. If already `Assigned`, a `ConflictException (409 CONFLICT)` is thrown.
4. Atomicity: The assignment creation and asset status transition to `Assigned` are executed in a single `@Transactional` boundary.

### Q2: How do you prevent assigning retired or in-repair assets?
**Answer**:
In `AssetService.assignAsset()`, the asset status is evaluated before assignment. If status is `Retired` or `In Repair`, an `InvalidOperationException (400 BAD_REQUEST)` is thrown immediately with a clear explanation message.

### Q3: How is ticket-asset linking validated?
**Answer**:
In `TicketAssetValidationService`, when linking an asset to a ticket (or filing a ticket with an asset ID):
1. The asset ID is checked for existence in the database (`404 NOT_FOUND` if absent).
2. The asset status is validated: retired assets cannot be linked to active/open tickets (`InvalidOperationException`).
3. Requesters can see their assigned assets directly and file tickets with pre-linked assets.
