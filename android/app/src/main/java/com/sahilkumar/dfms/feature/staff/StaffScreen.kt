package com.sahilkumar.dfms.feature.staff

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sahilkumar.dfms.R
import com.sahilkumar.dfms.core.ui.components.EmptyState
import com.sahilkumar.dfms.core.ui.components.ErrorState
import com.sahilkumar.dfms.core.ui.components.LoadingState
import com.sahilkumar.dfms.core.ui.components.StatusBadge
import com.sahilkumar.dfms.core.ui.components.statusColor
import com.sahilkumar.dfms.model.UserResponse
import kotlinx.coroutines.launch

@Composable
fun StaffScreen(viewModel: StaffViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var addOrEdit by remember { mutableStateOf<StaffEditTarget?>(null) }
    var resetTarget by remember { mutableStateOf<UserResponse?>(null) }

    fun handle(result: Result<Unit>) {
        result.onFailure { e -> scope.launch { snackbar.showSnackbar(e.message ?: "Error") } }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            FloatingActionButton(onClick = { addOrEdit = StaffEditTarget(null) }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.staff_add))
            }
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.loading -> LoadingState()
                state.error != null -> ErrorState(state.error!!, onRetry = viewModel::load)
                state.staff.isEmpty() -> EmptyState(stringResource(R.string.staff_add))
                else -> LazyColumn(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(state.staff) { staff ->
                        StaffRow(
                            staff = staff,
                            onEdit = { addOrEdit = StaffEditTarget(staff) },
                            onReset = { resetTarget = staff },
                            onToggle = {
                                if (staff.enabled) viewModel.disable(staff.id) { handle(it) }
                                else viewModel.enable(staff.id) { handle(it) }
                            },
                            onDelete = { viewModel.delete(staff.id) { handle(it) } },
                        )
                    }
                }
            }
        }
    }

    addOrEdit?.let { target ->
        StaffFormDialog(
            existing = target.staff,
            onDismiss = { addOrEdit = null },
            onCreate = { req -> viewModel.create(req) { r -> handle(r); if (r.isSuccess) addOrEdit = null } },
            onUpdate = { req -> target.staff?.let { s -> viewModel.update(s.id, req) { r -> handle(r); if (r.isSuccess) addOrEdit = null } } },
        )
    }
    resetTarget?.let { staff ->
        StaffResetDialog(
            name = staff.fullName,
            onDismiss = { resetTarget = null },
            onSubmit = { pass -> viewModel.resetPassword(staff.id, pass) { r -> handle(r); if (r.isSuccess) resetTarget = null } },
        )
    }
}

internal data class StaffEditTarget(val staff: UserResponse?)

@Composable
private fun StaffRow(
    staff: UserResponse,
    onEdit: () -> Unit,
    onReset: () -> Unit,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
) {
    var menu by remember { mutableStateOf(false) }
    OutlinedCard(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(staff.fullName, fontWeight = FontWeight.SemiBold)
                Text("@${staff.username}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                staff.phone?.takeIf { it.isNotBlank() }?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            StatusBadge(
                text = if (staff.enabled) stringResource(R.string.common_active) else stringResource(R.string.staff_status_disabled),
                color = statusColor(if (staff.enabled) "ACTIVE" else "DISABLED"),
            )
            Box {
                IconButton(onClick = { menu = true }) { Icon(Icons.Filled.MoreVert, contentDescription = null) }
                DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                    DropdownMenuItem(text = { Text(stringResource(R.string.action_edit)) }, onClick = { menu = false; onEdit() })
                    DropdownMenuItem(text = { Text(stringResource(R.string.staff_reset)) }, onClick = { menu = false; onReset() })
                    DropdownMenuItem(
                        text = { Text(stringResource(if (staff.enabled) R.string.staff_disable else R.string.staff_enable)) },
                        onClick = { menu = false; onToggle() },
                    )
                    DropdownMenuItem(text = { Text(stringResource(R.string.action_delete)) }, onClick = { menu = false; onDelete() })
                }
            }
        }
    }
}
