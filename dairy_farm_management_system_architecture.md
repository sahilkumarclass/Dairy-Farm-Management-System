# Dairy Farm Management System Architecture

## 1. System Overview

The product has 3 main parts:

```text
Customers App/Web
        |
        v
Frontend Application
(Owner Panel + Customer Panel)
        |
        v
Backend API Server
        |
------------------------------------------------
|              |               |               |
v              v               v               v
PostgreSQL   Auth Service   File Storage   Notification Service
Database     JWT/Auth       Bills/Images   WhatsApp/SMS
```

---

# 2. Core Architecture

## Frontend Layer

Two roles inside same app:

### A. Owner Panel
Used by dairy owner/staff for:
- Milk entries
- Expenses
- Billing
- Cow management
- Reports
- Customer management

### B. Customer Panel
Used by customers for:
- Milk history
- Monthly bills
- Payment tracking
- Notifications

---

# 3. Backend Architecture

Use modular backend structure.

```text
Backend
|
|-- Auth Module
|-- Customer Module
|-- Milk Sales Module
|-- Stock Module
|-- Billing Module
|-- Expense Module
|-- Cow Module
|-- Reports Module
|-- Notification Module
|-- Payment Module
```

Each module should contain:

```text
module/
|
|-- controller
|-- service
|-- repository
|-- validation
|-- routes
|-- dto/schema
```

---

# 4. Database Architecture

Main database entities:

## Users
Stores:
- owner
- staff
- customers

---

## Customers

```text
Customer
- id
- name
- phone
- address
- customMilkRate
- status
- createdAt
```

---

## Milk Entries

Tracks daily milk taken by customers.

```text
MilkEntry
- id
- customerId
- milkType
- quantity
- ratePerLiter
- totalAmount
- session (morning/evening)
- createdAt
```

---

## Milk Stock

```text
MilkStock
- id
- totalCollected
- totalSold
- remainingStock
- spoiledMilk
- date
```

---

## Expenses

```text
Expense
- id
- category
- amount
- notes
- expenseDate
```

---

## Cows

```text
Cow
- id
- breed
- age
- healthStatus
- milkProduction
```

---

## Vaccinations

```text
Vaccination
- id
- cowId
- vaccineName
- vaccinationDate
- nextDueDate
```

---

## Bills

```text
Bill
- id
- customerId
- month
- totalLiters
- totalAmount
- paidAmount
- remainingAmount
- status
```

---

## Payments

```text
Payment
- id
- billId
- amount
- paymentMethod
- paymentDate
```

---

# 5. Request Flow Architecture

Example:
Customer takes milk.

```text
Frontend
   |
   v
Milk Entry API
   |
   v
Validation Layer
   |
   v
Service Layer
   |
   v
Database Transaction
   |
   |-- Create Milk Entry
   |-- Update Stock
   |-- Update Bill
   |
   v
Response
```

---

# 6. Authentication Architecture

## Roles

```text
- OWNER
- STAFF
- CUSTOMER
```

---

## Access Control

| Module | Owner | Staff | Customer |
|---|---|---|---|
| Dashboard | Yes | Yes | Limited |
| Milk Entry | Yes | Yes | No |
| Expenses | Yes | Limited | No |
| Bills | Yes | Yes | Own Only |
| Reports | Yes | Limited | No |

---

# 7. Billing Engine Architecture

Monthly bill generation process:

```text
Cron Job
   |
   v
Fetch Monthly Milk Entries
   |
   v
Group By Customer
   |
   v
Calculate:
- total liters
- average/custom rates
- pending amounts
   |
   v
Generate Bill
```

---

# 8. Notification Architecture

Triggers:
- Pending payment
- Bill generated
- Low milk stock
- Vaccination due

Flow:

```text
Event Trigger
     |
     v
Notification Service
     |
-------------------------
|                       |
v                       v
WhatsApp              SMS
```

---

# 9. Analytics Architecture

Daily aggregation tables:

```text
DailySalesSummary
MonthlyExpenseSummary
ProfitLossSummary
```

Avoid calculating everything live every time.

Use:
- scheduled jobs
- cached summaries

for fast dashboard loading.

---

# 10. File Storage Architecture

Store:
- invoices
- expense bill photos
- cow documents

Structure:

```text
uploads/
|
|-- invoices
|-- expenses
|-- cows
```

---

# 11. Scalability Architecture

Future-ready design:

```text
API Layer
   |
Service Layer
   |
Repository Layer
   |
Database
```

Benefits:
- easier maintenance
- scalable
- reusable
- clean testing

---

# 12. Recommended Core Features Priority

## Phase 1 (MVP)
Build first:
1. Authentication
2. Customer management
3. Milk entry system
4. Expense management
5. Billing
6. Dashboard

---

## Phase 2
Add:
- customer login
- analytics
- notifications
- reports
- stock management

---

## Phase 3
Advanced:
- WhatsApp integration
- mobile app
- barcode/QR customer system
- AI forecasting
- multi-farm support

---

# 13. Important Business Logic

## Milk Entry Logic

```text
totalAmount = liters × customerRate
```

---

## Monthly Bill Logic

```text
monthlyBill =
sum(all milk entries)
-
paymentsAlreadyMade
```

---

## Profit Calculation

```text
profit =
totalSales
-
totalExpenses
```

---

# 14. Recommended UX Flow

## Daily Owner Workflow

```text
Open Dashboard
   |
Quick Customer Search
   |
Enter Milk Quantity
   |
Auto Amount Calculation
   |
Save
```

Should take:
- less than 5 seconds per customer entry.

---

# 15. Future Expansion Support

Architecture should support:
- multiple dairy branches
- multiple staff accounts
- mobile apps
- subscription plans
- online payments
- IoT milk measurement devices
- offline sync support

