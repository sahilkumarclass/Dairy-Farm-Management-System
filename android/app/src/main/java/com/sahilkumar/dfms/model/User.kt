package com.sahilkumar.dfms.model

data class UserResponse(
    val id: String,
    val username: String,
    val fullName: String,
    val phone: String?,
    val role: Role,
    val enabled: Boolean,
    val createdAt: String?,
)

data class StaffCreateRequest(
    val username: String,
    val password: String,
    val fullName: String,
    val phone: String?,
)

data class StaffUpdateRequest(
    val fullName: String,
    val phone: String?,
)

data class ResetPasswordRequest(
    val password: String,
)
