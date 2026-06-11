package com.sahilkumar.dfms.model

data class LoginRequest(
    val username: String,
    val password: String,
)

data class AuthResponse(
    val accessToken: String?,
    val tokenType: String?,
    val expiresInSeconds: Long = 0,
    val userId: String,
    val username: String,
    val fullName: String,
    val role: Role,
)
