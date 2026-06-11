package com.sahilkumar.dfms.feature.bills

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sahilkumar.dfms.core.network.ApiService
import com.sahilkumar.dfms.core.util.PagePagingSource
import com.sahilkumar.dfms.feature.customers.mapError
import com.sahilkumar.dfms.model.BillResponse
import com.sahilkumar.dfms.model.GenerateBillsRequest
import com.sahilkumar.dfms.model.PaymentRequest
import com.sahilkumar.dfms.model.PaymentResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillsViewModel @Inject constructor(
    private val api: ApiService,
) : ViewModel() {

    val bills: Flow<PagingData<BillResponse>> =
        Pager(PagingConfig(pageSize = 20)) {
            PagePagingSource { page, size -> api.bills(null, null, null, null, page, size) }
        }.flow.cachedIn(viewModelScope)

    fun generate(month: Int, year: Int, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(runCatching { api.generateBills(GenerateBillsRequest(month, year)); Unit }.mapError())
        }
    }

    fun recordPayment(billId: String, req: PaymentRequest, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(runCatching { api.recordPayment(billId, req); Unit }.mapError())
        }
    }

    fun loadPayments(billId: String, onResult: (Result<List<PaymentResponse>>) -> Unit) {
        viewModelScope.launch {
            onResult(runCatching { api.billPayments(billId) }.mapError())
        }
    }
}
