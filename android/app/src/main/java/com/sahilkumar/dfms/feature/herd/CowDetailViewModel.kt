package com.sahilkumar.dfms.feature.herd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sahilkumar.dfms.core.network.ApiService
import com.sahilkumar.dfms.core.util.userMessage
import com.sahilkumar.dfms.feature.customers.mapError
import com.sahilkumar.dfms.model.CowDetailResponse
import com.sahilkumar.dfms.model.CowHealthLogRequest
import com.sahilkumar.dfms.model.CowHealthLogResponse
import com.sahilkumar.dfms.model.CowMilkProductionRequest
import com.sahilkumar.dfms.model.CowMilkProductionResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CowDetailUiState(
    val loading: Boolean = true,
    val detail: CowDetailResponse? = null,
    val productions: List<CowMilkProductionResponse> = emptyList(),
    val healthLogs: List<CowHealthLogResponse> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class CowDetailViewModel @Inject constructor(
    private val api: ApiService,
) : ViewModel() {

    private val _state = MutableStateFlow(CowDetailUiState())
    val state: StateFlow<CowDetailUiState> = _state.asStateFlow()

    fun load(cowId: String) {
        if (cowId.isBlank()) return
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            runCatching {
                val detail = api.cowDetail(cowId)
                val productions = api.cowProduction(cowId, null, null, 0, 50).content
                val health = api.cowHealth(cowId, 0, 50).content
                Triple(detail, productions, health)
            }.onSuccess { (d, p, h) ->
                _state.value = CowDetailUiState(loading = false, detail = d, productions = p, healthLogs = h)
            }.onFailure {
                _state.value = CowDetailUiState(loading = false, error = it.userMessage())
            }
        }
    }

    fun addProduction(cowId: String, req: CowMilkProductionRequest, onResult: (Result<Unit>) -> Unit) =
        act(cowId, onResult) { api.createCowProduction(req) }

    fun addHealth(cowId: String, req: CowHealthLogRequest, onResult: (Result<Unit>) -> Unit) =
        act(cowId, onResult) { api.createCowHealth(req) }

    fun deleteProduction(cowId: String, id: String, onResult: (Result<Unit>) -> Unit) =
        act(cowId, onResult) { api.deleteCowProduction(id) }

    fun deleteHealth(cowId: String, id: String, onResult: (Result<Unit>) -> Unit) =
        act(cowId, onResult) { api.deleteCowHealth(id) }

    private fun act(cowId: String, onResult: (Result<Unit>) -> Unit, block: suspend () -> Unit) {
        viewModelScope.launch {
            val result = runCatching { block(); Unit }.mapError()
            result.onSuccess { load(cowId) }
            onResult(result)
        }
    }
}
