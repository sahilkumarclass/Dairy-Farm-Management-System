package com.sahilkumar.dfms.feature.milk

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
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.material3.SnackbarHostState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.sahilkumar.dfms.R
import com.sahilkumar.dfms.core.ui.components.PagedListScaffold
import com.sahilkumar.dfms.core.ui.components.StatusBadge
import com.sahilkumar.dfms.core.ui.theme.BrandGreen
import com.sahilkumar.dfms.core.util.formatDate
import com.sahilkumar.dfms.core.util.formatLiters
import com.sahilkumar.dfms.core.util.formatMoney
import com.sahilkumar.dfms.model.MilkEntryResponse
import kotlinx.coroutines.launch

@Composable
fun MilkEntriesScreen(viewModel: MilkViewModel = hiltViewModel()) {
    val items = viewModel.entries.collectAsLazyPagingItems()
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var editing by remember { mutableStateOf<MilkEntryResponse?>(null) }
    var showForm by remember { mutableStateOf(false) }

    fun handle(result: Result<Unit>) {
        result.onSuccess {
            items.refresh()
            scope.launch { snackbar.showSnackbar("OK") }
        }.onFailure { e -> scope.launch { snackbar.showSnackbar(e.message ?: "Error") } }
    }

    PagedListScaffold(
        items = items,
        emptyMessage = stringResource(R.string.milk_add),
        snackbar = snackbar,
        onAdd = { editing = null; showForm = true },
    ) { entry ->
        OutlinedCard(Modifier.fillMaxWidth()) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(entry.customerName, fontWeight = FontWeight.SemiBold)
                    Text(
                        "${formatDate(entry.entryDate)} · ${entry.session.name} · ${entry.milkType.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        "${formatLiters(entry.quantityLiters)} · ${formatMoney(entry.totalAmount)}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                StatusBadge(entry.session.name, BrandGreen)
                IconButton(onClick = { editing = entry; showForm = true }) {
                    Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.action_edit))
                }
                IconButton(onClick = { viewModel.delete(entry.id) { handle(it) } }) {
                    Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_delete))
                }
            }
        }
    }

    if (showForm) {
        MilkFormDialog(
            existing = editing,
            customers = customers,
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
