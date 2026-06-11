package com.sahilkumar.dfms.feature.herd

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.sahilkumar.dfms.R
import com.sahilkumar.dfms.core.ui.components.PagedListScaffold
import com.sahilkumar.dfms.core.ui.components.StatusBadge
import com.sahilkumar.dfms.core.ui.components.showError
import com.sahilkumar.dfms.core.ui.components.showSuccess
import com.sahilkumar.dfms.core.ui.components.statusColor
import com.sahilkumar.dfms.core.util.userMessage
import com.sahilkumar.dfms.model.CowResponse
import kotlinx.coroutines.launch

@Composable
fun HerdScreen(
    onOpenCow: (String) -> Unit,
    viewModel: HerdViewModel = hiltViewModel(),
) {
    val items = viewModel.cows.collectAsLazyPagingItems()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val savedMsg = stringResource(R.string.saved)

    var editing by remember { mutableStateOf<CowResponse?>(null) }
    var showForm by remember { mutableStateOf(false) }

    fun handle(result: Result<Unit>) {
        result.onSuccess {
            items.refresh()
            scope.launch { snackbar.showSuccess(savedMsg) }
        }.onFailure { e -> scope.launch { snackbar.showError(e.userMessage()) } }
    }

    PagedListScaffold(
        items = items,
        emptyMessage = stringResource(R.string.herd_add),
        snackbar = snackbar,
        onAdd = { editing = null; showForm = true },
    ) { cow ->
        OutlinedCard(Modifier.fillMaxWidth().clickable { onOpenCow(cow.id) }) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        cow.tagNo,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    val sub = listOfNotNull(cow.name, cow.breed).joinToString(" · ")
                    if (sub.isNotBlank()) {
                        Text(
                            sub,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                StatusBadge(cow.healthStatus.name, statusColor(cow.healthStatus.name))
                IconButton(onClick = { editing = cow; showForm = true }) {
                    Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.action_edit))
                }
            }
        }
    }

    if (showForm) {
        CowFormDialog(
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
