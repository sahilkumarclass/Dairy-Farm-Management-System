package com.sahilkumar.dfms.model

import java.math.BigDecimal

data class MilkEntryResponse(
    val id: String,
    val customerId: String,
    val customerName: String,
    val milkType: MilkType,
    val quantityLiters: BigDecimal,
    val ratePerLiter: BigDecimal?,
    val totalAmount: BigDecimal,
    val session: MilkSession,
    val entryDate: String,
    val notes: String?,
)

data class MilkEntryRequest(
    val customerId: String,
    val milkType: MilkType,
    val quantityLiters: BigDecimal,
    val ratePerLiter: BigDecimal?,
    val session: MilkSession,
    val entryDate: String,
    val notes: String?,
)
