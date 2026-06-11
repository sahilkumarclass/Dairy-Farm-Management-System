package com.sahilkumar.dfms.feature.staff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sahilkumar.dfms.core.network.ApiService
import com.sahilkumar.dfms.core.util.userMessage
import com.sahilkumar.dfms.feature.customers.mapError
import com.sahilkumar.dfms.model.ResetPasswordRequest
import com.sahilkumar.dfms.model.StaffCreateRequest
import com.sahilkumar.dfms.model.StaffUpdateRequest
import com.sahilkumar.dfms.model.UserResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StaffUiState(
    val loading: Boolean = true,
    val staff: List<UserResponse> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class StaffViewModel @Inject constructor(
    private val api: ApiService,
) : ViewModel() {

    private val _state = MutableStateFlow(StaffUiState())
    val state: StateFlow<StaffUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            runCatching { api.staff() }
                .onSuccess { _state.value = StaffUiState(loading = false, staff = it) }
                .onFailure { _state.value = StaffUiState(loading = false, error = it.userMessage()) }
        }
    }

    fun create(req: StaffCreateRequest, onResult: (Result<Unit>) -> Unit) = act(onResult) { api.createStaff(req) }
    fun update(id: String, req: StaffUpdateRequest, onResult: (Result<Unit>) -> Unit) = act(onResult) { api.updateStaff(id, req) }
    fun enable(id: String, onResult: (Result<Unit>) -> Unit) = act(onResult) { api.enableStaff(id) }
    fun disable(id: String, onResult: (Result<Unit>) -> Unit) = act(onResult) { api.disableStaff(id) }
    fun delete(id: String, onResult: (Result<Unit>) -> Unit) = act(onResult) { api.deleteStaff(id) }
    fun resetPassword(id: String, password: String, onResult: (Result<Unit>) -> Unit) =
        act(onResult) { api.resetStaffPassword(id, ResetPasswordRequest(password)) }

    private fun act(onResult: (Result<Unit>) -> Unit, block: suspend () -> Unit) {
        viewModelScope.launch {
            val result = runCatching { block(); Unit }.mapError()
            result.onSuccess { load() }
            onResult(result)
        }
    }
}
