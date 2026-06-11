package com.sahilkumar.dfms.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sahilkumar.dfms.core.data.AuthRepository
import com.sahilkumar.dfms.core.util.userMessage
import com.sahilkumar.dfms.model.Role
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onUsername(value: String) = _state.update { it.copy(username = value, error = null) }
    fun onPassword(value: String) = _state.update { it.copy(password = value, error = null) }

    fun login(roleErrorMessage: String) {
        val s = _state.value
        if (s.username.isBlank() || s.password.isBlank() || s.loading) return
        _state.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            authRepository.login(s.username, s.password)
                .onSuccess { role ->
                    if (role == Role.CUSTOMER) {
                        authRepository.logout()
                        _state.update { it.copy(loading = false, error = roleErrorMessage) }
                    } else {
                        // On OWNER/STAFF success the SessionManager state flips and nav
                        // reacts. Reset to a clean state so this Activity-scoped, retained
                        // ViewModel doesn't show a stale spinner if the user logs out and
                        // returns to the login screen.
                        _state.value = LoginUiState()
                    }
                }
                .onFailure { t ->
                    _state.update { it.copy(loading = false, error = t.userMessage()) }
                }
        }
    }
}
