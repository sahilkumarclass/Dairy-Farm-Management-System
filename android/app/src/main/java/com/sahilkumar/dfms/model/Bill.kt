package com.sahilkumar.dfms.model

import java.math.BigDecimal

data class BillResponse(
    val id: String,
    val customerId: String,
    val customerName: String,
    val periodMonth: Int,
    val periodYear: Int,
    val totalLiters: BigDecimal,
    val totalAmount: BigDecimal,
    val paidAmount: BigDecimal,
    val remainingAmount: BigDecimal,
    val status: BillStatus,
    val generatedAt: String?,
)

data class GenerateBillsRequest(
    val month: Int,
    val year: Int,
)

data class PaymentResponse(
    val id: String,
    val billId: String,
    val amount: BigDecimal,
    val paymentMethod: PaymentMethod,
    val paymentDate: String,
    val reference: String?,
    val notes: String?,
)

data class PaymentRequest(
    val amount: BigDecimal,
    val paymentMethod: PaymentMethod,
    val paymentDate: String,
    val reference: String?,
    val notes: String?,
)
