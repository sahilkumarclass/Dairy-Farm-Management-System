package com.sahilkumar.dfms.feature.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sahilkumar.dfms.core.network.ApiService
import com.sahilkumar.dfms.core.util.PagePagingSource
import com.sahilkumar.dfms.core.util.userMessage
import com.sahilkumar.dfms.model.CreateCustomerLoginRequest
import com.sahilkumar.dfms.model.CustomerRequest
import com.sahilkumar.dfms.model.CustomerResponse
import com.sahilkumar.dfms.model.CustomerStatus
import com.sahilkumar.dfms.model.ResetPasswordRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class CustomersViewModel @Inject constructor(
    private val api: ApiService,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _status = MutableStateFlow<CustomerStatus?>(null)
    val status: StateFlow<CustomerStatus?> = _status.asStateFlow()

    val customers: Flow<PagingData<CustomerResponse>> =
        combine(_query.debounce(300), _status) { q, s -> q to s }
            .flatMapLatest { (q, s) ->
                Pager(PagingConfig(pageSize = 20)) {
                    PagePagingSource { page, size ->
                        api.customers(q.ifBlank { null }, s?.name, page, size)
                    }
                }.flow
            }
            .cachedIn(viewModelScope)

    fun onQuery(value: String) { _query.value = value }
    fun onStatus(value: CustomerStatus?) { _status.value = value }

    fun save(id: String?, req: CustomerRequest, onResult: (Result<Unit>) -> Unit) = run {
        viewModelScope.launch {
            onResult(runCatching {
                if (id == null) api.createCustomer(req) else api.updateCustomer(id, req)
                Unit
            }.mapError())
        }
    }

    fun createLogin(id: String, username: String, password: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(runCatching {
                api.createCustomerLogin(id, CreateCustomerLoginRequest(username.trim(), password)); Unit
            }.mapError())
        }
    }

    fun resetPassword(userId: String, password: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(runCatching { api.resetStaffPassword(userId, ResetPasswordRequest(password)) }.mapError())
        }
    }

    fun reactivate(id: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch { onResult(runCatching { api.reactivateCustomer(id); Unit }.mapError()) }
    }

    fun stop(id: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch { onResult(runCatching { api.stopCustomer(id) }.mapError()) }
    }

    fun deletePermanent(id: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch { onResult(runCatching { api.deleteCustomerPermanent(id) }.mapError()) }
    }
}

/** Wrap a failure into a Result carrying a user-facing message. */
internal fun <T> Result<T>.mapError(): Result<T> =
    fold(onSuccess = { Result.success(it) }, onFailure = { Result.failure(Exception(it.userMessage(), it)) })
