package com.sahilkumar.dfms.model

import java.math.BigDecimal

data class CustomerResponse(
    val id: String,
    val name: String,
    val phone: String,
    val address: String?,
    val customMilkRate: BigDecimal?,
    val status: CustomerStatus,
    val createdAt: String?,
    val userId: String?,
    val username: String?,
)

data class CustomerRequest(
    val name: String,
    val phone: String,
    val address: String?,
    val customMilkRate: BigDecimal?,
    val status: CustomerStatus,
)

data class CreateCustomerLoginRequest(
    val username: String,
    val password: String,
)
