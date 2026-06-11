package com.sahilkumar.dfms.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sahilkumar.dfms.core.network.ApiService
import com.sahilkumar.dfms.core.util.userMessage
import com.sahilkumar.dfms.model.DashboardSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val loading: Boolean = true,
    val data: DashboardSummary? = null,
    val error: String? = null,
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val api: ApiService,
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.value = DashboardUiState(loading = true)
        viewModelScope.launch {
            runCatching { api.dashboardSummary() }
                .onSuccess { _state.value = DashboardUiState(loading = false, data = it) }
                .onFailure { _state.value = DashboardUiState(loading = false, error = it.userMessage()) }
        }
    }
}
