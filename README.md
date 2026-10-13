# Vehicle Service Management System (AutoCare PRO)

A comprehensive, full-stack enterprise web application built for vehicle workshop automation, service booking, live status tracking, mechanic job scheduling, parts inventory management, invoicing, and simulated payments.

---

## 1. System Architecture & User Roles

The application implements strict Role-Based Access Control (RBAC) across four user roles:

1. **Customer (Vehicle Owner):**
   - Register & login with JWT authentication
   - Garage Management: Register, view, edit vehicles (License plate, brand, model, year, fuel type, mileage)
   - Browse catalog of service packages (Periodic Maintenance, Engine Repairs, Brake Overhaul, AC Care, Inspections)
   - Book appointment slots (Date, time slot, symptoms/problem description)
   - Live service status tracking (`PENDING` → `CONFIRMED` → `IN_PROGRESS` → `COMPLETED`)
   - Cancel eligible appointments
   - View tax invoices with parts and labor breakdown
   - Simulated payment gateway (UPI, Card, Cash) with instant confirmation
   - Notification center for booking confirmations, mechanic assignments, and payment receipts

2. **Service Advisor:**
   - Inbound service request triage & inspection
   - Approve bookings and set estimated service costs
   - Dispatch jobs to available workshop mechanics
   - Monitor job progression on the workshop floor
   - Update booking milestones & communicate updates

3. **Mechanic (Technician):**
   - Access restricted strictly to jobs assigned to the logged-in mechanic
   - Review vehicle history, customer complaints, and symptoms
   - Enter vehicle diagnosis notes & work progress
   - Record spare parts consumed from workshop inventory (auto-deducting stock)
   - Log labor charges
   - Update job status (`IN_PROGRESS`, `ON_HOLD`, `COMPLETED`)
   - Marking completed automatically triggers invoice calculation and logs service records

4. **Administrator (Workshop Executive):**
   - Live executive dashboard (Revenue, bookings, fleet size, technician count, inventory alerts)
   - User account management (View staff, create mechanic/advisor accounts, activate/deactivate accounts)
   - Service catalog management (Add/edit repair packages, base pricing, duration)
   - Spare parts & inventory control (Track stock, low-stock threshold badges, restock / adjustment dialogs)
   - Complete booking history, invoice logs, and payment reports

---

## 2. Technology Stack

- **Backend:** Java 17, Spring Boot 3.3.4, Spring Data JPA, Spring Security with stateless JWT
- **Database:** MySQL (`vehicle_service_db`) with automatic table creation & seed data
- **Frontend:** React 19, Vite, Lucide Icons, Canvas Confetti, Vanilla CSS Design System tokens
- **API Documentation:** Swagger / OpenAPI 3.0 UI (`http://localhost:8080/swagger-ui.html`)
- **Build Tools:** Apache Maven (Backend) & npm / Vite (Frontend)

---

## 3. Pre-configured Demo Accounts

For instant testing and evaluation, the application provides pre-configured credentials and 1-click login buttons on the sign-in screen:

| Role | Email | Password | Preloaded Access |
|---|---|---|---|
| **Administrator** | `admin@vehicleservice.com` | `admin123` | Full system control, users, inventory, reports |
| **Service Advisor** | `advisor@vehicleservice.com` | `advisor123` | Booking requests, mechanic dispatch, estimates |
| **Mechanic** | `mechanic@vehicleservice.com` | `mechanic123` | Assigned jobs, diagnosis, parts usage, completion |
| **Customer** | `customer@vehicleservice.com` | `customer123` | Vehicles, booking scheduler, bills, payments |

---

## 4. Database Setup (MySQL)

1. Open **MySQL Workbench** or MySQL CLI.
2. Run the database schema script:
   ```sql
   source database/schema.sql;
   ```
3. (Optional) Run the seed data script:
   ```sql
   source database/seed_data.sql;
   ```
   *Note: Spring Boot's built-in `DataSeeder` automatically initializes and hashes demo user accounts on initial startup.*

---

## 5. How to Run Locally

### Running the Backend (Spring Boot):
Navigate to the `backend/` directory and execute:
```powershell
# Using the Windows batch launcher:
.\mvnw.cmd spring-boot:run

# Or via Maven:
mvn -s settings.xml spring-boot:run
```
- REST API Server: `http://localhost:8080/api`
- Swagger OpenAPI Documentation: `http://localhost:8080/swagger-ui.html`

### Running the Frontend (React + Vite):
Navigate to the `frontend/` directory and execute:
```powershell
cd frontend
npm install
npm run dev
```
- Open your browser at: `http://localhost:5173`

---

## 6. End-to-End Workflow Demonstration

1. **Step 1 - Customer:** Log in as `customer@vehicleservice.com`, register a vehicle, and schedule a service appointment.
2. **Step 2 - Service Advisor:** Switch to `advisor@vehicleservice.com` via the sidebar role switcher, inspect the request, and assign it to Mechanic **Vikram Singh**.
3. **Step 3 - Mechanic:** Switch to `mechanic@vehicleservice.com`, open the assigned job, add inspection diagnosis notes, attach spare parts used from inventory, and mark status `COMPLETED`.
4. **Step 4 - Invoice & Payment:** Switch back to `customer@vehicleservice.com`, open the **Invoices & Billing** tab, preview the generated tax invoice, and complete payment via simulated UPI/Card/Cash.
5. **Step 5 - Admin Reports:** Log in as `admin@vehicleservice.com` to review the updated workshop revenue, booking statistics, and inventory consumption.
