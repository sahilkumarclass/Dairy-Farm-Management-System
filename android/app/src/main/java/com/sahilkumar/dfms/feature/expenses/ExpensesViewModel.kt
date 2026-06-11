package com.sahilkumar.dfms.feature.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sahilkumar.dfms.core.network.ApiService
import com.sahilkumar.dfms.core.util.PagePagingSource
import com.sahilkumar.dfms.feature.customers.mapError
import com.sahilkumar.dfms.model.ExpenseRequest
import com.sahilkumar.dfms.model.ExpenseResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val api: ApiService,
) : ViewModel() {

    private val _category = MutableStateFlow<String?>(null)
    val category: StateFlow<String?> = _category.asStateFlow()

    val expenses: Flow<PagingData<ExpenseResponse>> =
        _category.flatMapLatest { cat ->
            Pager(PagingConfig(pageSize = 20)) {
                PagePagingSource { page, size -> api.expenses(cat, null, null, page, size) }
            }.flow
        }.cachedIn(viewModelScope)

    fun onCategory(value: String?) { _category.value = value }

    fun save(id: String?, req: ExpenseRequest, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(runCatching {
                if (id == null) api.createExpense(req) else api.updateExpense(id, req); Unit
            }.mapError())
        }
    }

    fun delete(id: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch { onResult(runCatching { api.deleteExpense(id) }.mapError()) }
    }
}
