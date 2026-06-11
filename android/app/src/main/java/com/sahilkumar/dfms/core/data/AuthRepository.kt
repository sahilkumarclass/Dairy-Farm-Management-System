package com.sahilkumar.dfms.core.data

import com.sahilkumar.dfms.core.network.ApiService
import com.sahilkumar.dfms.model.LoginRequest
import com.sahilkumar.dfms.model.Role
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: ApiService,
    private val session: SessionManager,
) {
    suspend fun login(username: String, password: String): Result<Role> = runCatching {
        val auth = api.login(LoginRequest(username.trim(), password))
        session.save(auth)
        auth.role
    }

    suspend fun logout() = session.clear()
}
