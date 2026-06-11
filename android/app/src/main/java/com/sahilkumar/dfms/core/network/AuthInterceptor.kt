package com.sahilkumar.dfms.core.network

import com.sahilkumar.dfms.core.data.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/** Adds the Bearer token to every request and force-logs-out on a 401. */
@Singleton
class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = sessionManager.token
        val request = if (token.isNullOrBlank() || original.header("Authorization") != null) {
            original
        } else {
            original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        }

        val response = chain.proceed(request)
        if (response.code == 401 && !original.url.encodedPath.endsWith("/auth/login")) {
            sessionManager.forceLogout()
        }
        return response
    }
}
