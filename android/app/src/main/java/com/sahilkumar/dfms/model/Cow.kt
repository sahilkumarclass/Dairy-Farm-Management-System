package com.sahilkumar.dfms.model

import java.math.BigDecimal

data class CowResponse(
    val id: String,
    val tagNo: String,
    val name: String?,
    val breed: String?,
    val gender: Gender,
    val ageMonths: Int?,
    val healthStatus: HealthStatus,
    val dailyYieldEstimateLiters: BigDecimal?,
    val dateAcquired: String?,
    val notes: String?,
)

data class CowRequest(
    val tagNo: String,
    val name: String?,
    val breed: String?,
    val gender: Gender,
    val ageMonths: Int?,
    val healthStatus: HealthStatus,
    val dailyYieldEstimateLiters: BigDecimal?,
    val dateAcquired: String?,
    val notes: String?,
)

data class CowDetailResponse(
    val cow: CowResponse,
    val monthLiters: BigDecimal,
    val lifetimeLiters: BigDecimal,
    val monthHealthCost: BigDecimal,
    val monthExpenseTotal: BigDecimal,
)

data class CowSummary(
    val totalCows: Long,
    val healthyCount: Long,
    val underTreatmentCount: Long,
    val dryCount: Long,
    val monthLiters: BigDecimal,
    val monthHealthCost: BigDecimal,
    val monthFeedAndOtherCost: BigDecimal,
)

data class CowHealthLogResponse(
    val id: String,
    val cowId: String,
    val cowTagNo: String,
    val eventType: HealthEventType,
    val eventDate: String,
    val nextDueDate: String?,
    val vetName: String?,
    val cost: BigDecimal?,
    val notes: String?,
)

data class CowHealthLogRequest(
    val cowId: String,
    val eventType: HealthEventType,
    val eventDate: String,
    val nextDueDate: String?,
    val vetName: String?,
    val cost: BigDecimal?,
    val notes: String?,
)

data class CowMilkProductionResponse(
    val id: String,
    val cowId: String,
    val cowTagNo: String,
    val productionDate: String,
    val session: MilkSession,
    val liters: BigDecimal,
    val notes: String?,
)

data class CowMilkProductionRequest(
    val cowId: String,
    val productionDate: String,
    val session: MilkSession,
    val liters: BigDecimal,
    val notes: String?,
)
