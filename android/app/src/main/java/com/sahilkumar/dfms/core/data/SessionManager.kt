package com.sahilkumar.dfms.core.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sahilkumar.dfms.model.AuthResponse
import com.sahilkumar.dfms.model.Role
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private val Context.authDataStore by preferencesDataStore("dfms_auth")

/** The signed-in user, or null when logged out. */
data class Session(
    val token: String,
    val userId: String,
    val username: String,
    val fullName: String,
    val role: Role,
)

sealed interface SessionState {
    data object Loading : SessionState
    data object LoggedOut : SessionState
    data class LoggedIn(val session: Session) : SessionState
}

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val ds = context.authDataStore
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _state = MutableStateFlow<SessionState>(SessionState.Loading)
    val state: StateFlow<SessionState> = _state.asStateFlow()

    /** Read synchronously by the OkHttp auth interceptor. */
    @Volatile
    var token: String? = null
        private set

    init {
        scope.launch {
            val prefs = ds.data.first()
            val token = prefs[KEY_TOKEN]
            if (token.isNullOrBlank()) {
                _state.value = SessionState.LoggedOut
            } else {
                this@SessionManager.token = token
                _state.value = SessionState.LoggedIn(
                    Session(
                        token = token,
                        userId = prefs[KEY_USER_ID].orEmpty(),
                        username = prefs[KEY_USERNAME].orEmpty(),
                        fullName = prefs[KEY_FULL_NAME].orEmpty(),
                        role = runCatching { Role.valueOf(prefs[KEY_ROLE].orEmpty()) }.getOrDefault(Role.STAFF),
                    ),
                )
            }
        }
    }

    suspend fun save(auth: AuthResponse) {
        val accessToken = auth.accessToken ?: return
        token = accessToken
        ds.edit { p ->
            p[KEY_TOKEN] = accessToken
            p[KEY_USER_ID] = auth.userId
            p[KEY_USERNAME] = auth.username
            p[KEY_FULL_NAME] = auth.fullName
            p[KEY_ROLE] = auth.role.name
        }
        _state.value = SessionState.LoggedIn(
            Session(accessToken, auth.userId, auth.username, auth.fullName, auth.role),
        )
    }

    suspend fun clear() {
        token = null
        ds.edit { it.clear() }
        _state.value = SessionState.LoggedOut
    }

    /** Called by the interceptor on a 401; clears the session off the IO scope. */
    fun forceLogout() {
        token = null
        scope.launch { clear() }
    }

    private companion object {
        val KEY_TOKEN = stringPreferencesKey("token")
        val KEY_USER_ID = stringPreferencesKey("userId")
        val KEY_USERNAME = stringPreferencesKey("username")
        val KEY_FULL_NAME = stringPreferencesKey("fullName")
        val KEY_ROLE = stringPreferencesKey("role")
    }
}
