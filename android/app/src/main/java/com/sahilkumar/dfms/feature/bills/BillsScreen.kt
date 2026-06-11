package com.sahilkumar.dfms.feature.bills

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.sahilkumar.dfms.R
import com.sahilkumar.dfms.core.ui.components.AppTextField
import com.sahilkumar.dfms.core.ui.components.DateField
import com.sahilkumar.dfms.core.ui.components.EnumDropdown
import com.sahilkumar.dfms.core.ui.components.FormDialog
import com.sahilkumar.dfms.core.ui.components.LabeledValue
import com.sahilkumar.dfms.core.ui.components.PagedListScaffold
import com.sahilkumar.dfms.core.ui.components.StatusBadge
import com.sahilkumar.dfms.core.ui.components.showError
import com.sahilkumar.dfms.core.ui.components.showSuccess
import com.sahilkumar.dfms.core.ui.components.statusColor
import com.sahilkumar.dfms.core.util.formatMoney
import com.sahilkumar.dfms.core.util.parseMoneyOrNull
import com.sahilkumar.dfms.core.util.todayIso
import com.sahilkumar.dfms.core.util.userMessage
import com.sahilkumar.dfms.model.BillResponse
import com.sahilkumar.dfms.model.PaymentMethod
import com.sahilkumar.dfms.model.PaymentRequest
import kotlinx.coroutines.launch

@Composable
fun BillsScreen(viewModel: BillsViewModel = hiltViewModel()) {
    val items = viewModel.bills.collectAsLazyPagingItems()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val savedMsg = stringResource(R.string.saved)

    var showGenerate by remember { mutableStateOf(false) }
    var payTarget by remember { mutableStateOf<BillResponse?>(null) }

    fun handle(result: Result<Unit>) {
        result.onSuccess {
            items.refresh()
            scope.launch { snackbar.showSuccess(savedMsg) }
        }.onFailure { e -> scope.launch { snackbar.showError(e.userMessage()) } }
    }

    PagedListScaffold(
        items = items,
        emptyMessage = stringResource(R.string.bills_generate),
        snackbar = snackbar,
        onAdd = { showGenerate = true },
    ) { bill ->
        OutlinedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            bill.customerName,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            "${bill.periodMonth}/${bill.periodYear}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    StatusBadge(bill.status.name, statusColor(bill.status.name))
                }
                LabeledValue(stringResource(R.string.bill_total), formatMoney(bill.totalAmount))
                LabeledValue(stringResource(R.string.bill_paid), formatMoney(bill.paidAmount))
                LabeledValue(stringResource(R.string.bill_remaining), formatMoney(bill.remainingAmount))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { payTarget = bill }) {
                        Text(stringResource(R.string.bill_record_payment))
                    }
                }
            }
        }
    }

    if (showGenerate) {
        GenerateBillsDialog(
            onDismiss = { showGenerate = false },
            onSubmit = { month, year ->
                viewModel.generate(month, year) { result ->
                    handle(result)
                    if (result.isSuccess) showGenerate = false
                }
            },
        )
    }

    payTarget?.let { bill ->
        RecordPaymentDialog(
            bill = bill,
            onDismiss = { payTarget = null },
            onSubmit = { req ->
                viewModel.recordPayment(bill.id, req) { result ->
                    handle(result)
                    if (result.isSuccess) payTarget = null
                }
            },
        )
    }
}

@Composable
private fun GenerateBillsDialog(
    onDismiss: () -> Unit,
    onSubmit: (month: Int, year: Int) -> Unit,
) {
    var month by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    val m = month.toIntOrNull()
    val y = year.toIntOrNull()
    val canSave = m != null && m in 1..12 && y != null && y in 2000..2100

    FormDialog(
        title = stringResource(R.string.bills_generate),
        onDismiss = onDismiss,
        onConfirm = { if (m != null && y != null) onSubmit(m, y) },
        confirmEnabled = canSave,
        confirmText = stringResource(R.string.action_generate),
    ) {
        AppTextField(month, { month = it.filter(Char::isDigit).take(2) }, stringResource(R.string.bill_month), keyboardType = KeyboardType.Number)
        AppTextField(year, { year = it.filter(Char::isDigit).take(4) }, stringResource(R.string.bill_year), keyboardType = KeyboardType.Number)
    }
}

@Composable
private fun RecordPaymentDialog(
    bill: BillResponse,
    onDismiss: () -> Unit,
    onSubmit: (PaymentRequest) -> Unit,
) {
    var amount by remember { mutableStateOf(bill.remainingAmount.toPlainString()) }
    var method by remember { mutableStateOf(PaymentMethod.CASH) }
    var date by remember { mutableStateOf(todayIso()) }
    var reference by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val amt = parseMoneyOrNull(amount)
    FormDialog(
        title = stringResource(R.string.bill_record_payment),
        onDismiss = onDismiss,
        onConfirm = {
            onSubmit(
                PaymentRequest(
                    amount = amt!!,
                    paymentMethod = method,
                    paymentDate = date,
                    reference = reference.ifBlank { null },
                    notes = notes.ifBlank { null },
                ),
            )
        },
        confirmEnabled = amt != null && amt.signum() > 0,
    ) {
        AppTextField(amount, { amount = it }, stringResource(R.string.common_amount), keyboardType = KeyboardType.Decimal)
        EnumDropdown(stringResource(R.string.payment_method), PaymentMethod.entries, method, { method = it }, display = { it.name })
        DateField(stringResource(R.string.common_date), date, { date = it })
        AppTextField(reference, { reference = it }, stringResource(R.string.payment_reference))
        AppTextField(notes, { notes = it }, stringResource(R.string.common_notes_optional))
    }
}
