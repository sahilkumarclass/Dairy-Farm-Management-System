package com.sahilkumar.dfms.model

import java.math.BigDecimal

data class ExpenseResponse(
    val id: String,
    val category: ExpenseCategory,
    val amount: BigDecimal,
    val notes: String?,
    val expenseDate: String,
    val cowId: String?,
    val cowTagNo: String?,
)

data class ExpenseRequest(
    val category: ExpenseCategory,
    val amount: BigDecimal,
    val notes: String?,
    val expenseDate: String,
    val cowId: String?,
)
