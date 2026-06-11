package com.sahilkumar.dfms.model

import java.math.BigDecimal

data class DashboardSummary(
    val today: String,
    val todayLiters: BigDecimal,
    val todaySales: BigDecimal,
    val monthLiters: BigDecimal,
    val monthSales: BigDecimal,
    val monthExpenses: BigDecimal,
    val monthProfit: BigDecimal,
    val outstandingDues: BigDecimal,
    val activeCustomers: Long,
)
