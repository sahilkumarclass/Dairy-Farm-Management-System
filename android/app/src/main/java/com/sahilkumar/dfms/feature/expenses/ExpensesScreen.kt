package com.sahilkumar.dfms.feature.expenses

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
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
import com.sahilkumar.dfms.core.ui.components.PagedListScaffold
import com.sahilkumar.dfms.core.ui.components.showError
import com.sahilkumar.dfms.core.ui.components.showSuccess
import com.sahilkumar.dfms.core.util.formatDate
import com.sahilkumar.dfms.core.util.formatMoney
import com.sahilkumar.dfms.core.util.parseMoneyOrNull
import com.sahilkumar.dfms.core.util.todayIso
import com.sahilkumar.dfms.core.util.userMessage
import com.sahilkumar.dfms.model.ExpenseCategory
import com.sahilkumar.dfms.model.ExpenseRequest
import com.sahilkumar.dfms.model.ExpenseResponse
import kotlinx.coroutines.launch

@Composable
fun ExpensesScreen(viewModel: ExpensesViewModel = hiltViewModel()) {
    val items = viewModel.expenses.collectAsLazyPagingItems()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val savedMsg = stringResource(R.string.saved)

    var editing by remember { mutableStateOf<ExpenseResponse?>(null) }
    var showForm by remember { mutableStateOf(false) }

    fun handle(result: Result<Unit>) {
        result.onSuccess {
            items.refresh()
            scope.launch { snackbar.showSuccess(savedMsg) }
        }.onFailure { e -> scope.launch { snackbar.showError(e.userMessage()) } }
    }

    PagedListScaffold(
        items = items,
        emptyMessage = stringResource(R.string.expenses_add),
        snackbar = snackbar,
        onAdd = { editing = null; showForm = true },
    ) { expense ->
        OutlinedCard(Modifier.fillMaxWidth()) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        expense.category.name,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(formatDate(expense.expenseDate), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    expense.notes?.takeIf { it.isNotBlank() }?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Text(formatMoney(expense.amount), fontWeight = FontWeight.SemiBold)
                IconButton(onClick = { editing = expense; showForm = true }) {
                    Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.action_edit))
                }
                IconButton(onClick = { viewModel.delete(expense.id) { handle(it) } }) {
                    Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_delete))
                }
            }
        }
    }

    if (showForm) {
        ExpenseFormDialog(
            existing = editing,
            onDismiss = { showForm = false },
            onSave = { req ->
                viewModel.save(editing?.id, req) { result ->
                    handle(result)
                    if (result.isSuccess) showForm = false
                }
            },
        )
    }
}

@Composable
private fun ExpenseFormDialog(
    existing: ExpenseResponse?,
    onDismiss: () -> Unit,
    onSave: (ExpenseRequest) -> Unit,
) {
    var category by remember { mutableStateOf(existing?.category ?: ExpenseCategory.FEED) }
    var amount by remember { mutableStateOf(existing?.amount?.toPlainString().orEmpty()) }
    var notes by remember { mutableStateOf(existing?.notes.orEmpty()) }
    var date by remember { mutableStateOf(existing?.expenseDate ?: todayIso()) }

    val amt = parseMoneyOrNull(amount)
    FormDialog(
        title = stringResource(if (existing == null) R.string.expense_new else R.string.expense_edit),
        onDismiss = onDismiss,
        onConfirm = {
            onSave(
                ExpenseRequest(
                    category = category,
                    amount = amt!!,
                    notes = notes.ifBlank { null },
                    expenseDate = date,
                    cowId = existing?.cowId,
                ),
            )
        },
        confirmEnabled = amt != null && amt.signum() >= 0,
    ) {
        EnumDropdown(stringResource(R.string.expense_category), ExpenseCategory.entries, category, { category = it }, display = { it.name })
        AppTextField(amount, { amount = it }, stringResource(R.string.common_amount), keyboardType = KeyboardType.Decimal)
        DateField(stringResource(R.string.common_date), date, { date = it })
        AppTextField(notes, { notes = it }, stringResource(R.string.common_notes_optional))
    }
}
