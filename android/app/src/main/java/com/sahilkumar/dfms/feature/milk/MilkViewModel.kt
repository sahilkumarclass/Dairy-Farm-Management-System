package com.sahilkumar.dfms.feature.milk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sahilkumar.dfms.core.network.ApiService
import com.sahilkumar.dfms.core.util.PagePagingSource
import com.sahilkumar.dfms.core.util.userMessage
import com.sahilkumar.dfms.feature.customers.mapError
import com.sahilkumar.dfms.model.CustomerResponse
import com.sahilkumar.dfms.model.MilkEntryRequest
import com.sahilkumar.dfms.model.MilkEntryResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MilkViewModel @Inject constructor(
    private val api: ApiService,
) : ViewModel() {

    val entries: Flow<PagingData<MilkEntryResponse>> =
        Pager(PagingConfig(pageSize = 20)) {
            PagePagingSource { page, size -> api.milkEntries(null, null, null, page, size) }
        }.flow.cachedIn(viewModelScope)

    private val _customers = MutableStateFlow<List<CustomerResponse>>(emptyList())
    val customers: StateFlow<List<CustomerResponse>> = _customers.asStateFlow()

    init { loadCustomers() }

    private fun loadCustomers() {
        viewModelScope.launch {
            runCatching { api.customers(null, "ACTIVE", 0, 200) }
                .onSuccess { _customers.value = it.content }
        }
    }

    fun save(id: String?, req: MilkEntryRequest, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(runCatching {
                if (id == null) api.createMilkEntry(req) else api.updateMilkEntry(id, req); Unit
            }.mapError())
        }
    }

    fun delete(id: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch { onResult(runCatching { api.deleteMilkEntry(id) }.mapError()) }
    }
}
