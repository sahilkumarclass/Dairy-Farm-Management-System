package com.sahilkumar.dfms.feature.herd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sahilkumar.dfms.core.network.ApiService
import com.sahilkumar.dfms.core.util.PagePagingSource
import com.sahilkumar.dfms.feature.customers.mapError
import com.sahilkumar.dfms.model.CowRequest
import com.sahilkumar.dfms.model.CowResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HerdViewModel @Inject constructor(
    private val api: ApiService,
) : ViewModel() {

    val cows: Flow<PagingData<CowResponse>> =
        Pager(PagingConfig(pageSize = 20)) {
            PagePagingSource { page, size -> api.cows(null, null, page, size) }
        }.flow.cachedIn(viewModelScope)

    fun save(id: String?, req: CowRequest, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(runCatching {
                if (id == null) api.createCow(req) else api.updateCow(id, req); Unit
            }.mapError())
        }
    }

    fun delete(id: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch { onResult(runCatching { api.deleteCow(id) }.mapError()) }
    }
}
