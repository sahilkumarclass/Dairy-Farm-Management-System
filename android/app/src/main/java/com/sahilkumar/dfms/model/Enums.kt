package com.sahilkumar.dfms.model

/** Mirrors the backend enums exactly (names are serialized over the wire). */

enum class Role { OWNER, STAFF, CUSTOMER }

enum class CustomerStatus { ACTIVE, INACTIVE }

enum class MilkType { COW, BUFFALO, MIXED }

enum class MilkSession { MORNING, EVENING }

enum class ExpenseCategory { FEED, VETERINARY, LABOR, UTILITIES, EQUIPMENT, TRANSPORT, OTHER }

enum class BillStatus { UNPAID, PARTIAL, PAID }

enum class PaymentMethod { CASH, UPI, BANK_TRANSFER, CARD }

enum class Gender { FEMALE, MALE }

enum class HealthStatus { HEALTHY, UNDER_TREATMENT, DRY, SOLD, DECEASED }

enum class HealthEventType { VACCINATION, VET_VISIT, TREATMENT, CHECKUP, OTHER }
