package com.sahilkumar.dfms.feature.customers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.sahilkumar.dfms.R
import com.sahilkumar.dfms.core.ui.screenContentPadding
import com.sahilkumar.dfms.core.ui.screenPadding
import com.sahilkumar.dfms.core.ui.components.AppSnackbarHost
import com.sahilkumar.dfms.core.ui.components.EmptyState
import com.sahilkumar.dfms.core.ui.components.ErrorState
import com.sahilkumar.dfms.core.ui.components.LoadingState
import com.sahilkumar.dfms.core.ui.components.StatusBadge
import com.sahilkumar.dfms.core.ui.components.showError
import com.sahilkumar.dfms.core.ui.components.showSuccess
import com.sahilkumar.dfms.core.ui.components.statusColor
import com.sahilkumar.dfms.core.util.formatMoney
import com.sahilkumar.dfms.core.util.userMessage
import com.sahilkumar.dfms.model.CustomerResponse
import com.sahilkumar.dfms.model.CustomerStatus
import androidx.paging.LoadState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(viewModel: CustomersViewModel = hiltViewModel()) {
    val items = viewModel.customers.collectAsLazyPagingItems()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val status by viewModel.status.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val savedMsg = stringResource(R.string.saved)

    var addOrEdit by remember { mutableStateOf<EditTarget?>(null) }
    var loginTarget by remember { mutableStateOf<CustomerResponse?>(null) }
    var resetTarget by remember { mutableStateOf<CustomerResponse?>(null) }

    fun handle(result: Result<Unit>) {
        result.onSuccess {
            items.refresh()
            scope.launch { snackbar.showSuccess(savedMsg) }
        }.onFailure { e ->
            scope.launch { snackbar.showError(e.userMessage()) }
        }
    }

    Scaffold(
        snackbarHost = { AppSnackbarHost(snackbar) },
        floatingActionButton = {
            FloatingActionButton(onClick = { addOrEdit = EditTarget(null) }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.customers_add))
            }
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = query,
                onValueChange = viewModel::onQuery,
                label = { Text(stringResource(R.string.customers_search_hint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = screenPadding(), vertical = 8.dp),
            )
            Row(
                Modifier.fillMaxWidth().padding(horizontal = screenPadding()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterChip(selected = status == null, onClick = { viewModel.onStatus(null) }, label = { Text(stringResource(R.string.filter_all)) })
                FilterChip(selected = status == CustomerStatus.ACTIVE, onClick = { viewModel.onStatus(CustomerStatus.ACTIVE) }, label = { Text(stringResource(R.string.common_active)) })
                FilterChip(selected = status == CustomerStatus.INACTIVE, onClick = { viewModel.onStatus(CustomerStatus.INACTIVE) }, label = { Text(stringResource(R.string.common_inactive)) })
            }

            when (val refresh = items.loadState.refresh) {
                is LoadState.Loading -> LoadingState()
                is LoadState.Error -> ErrorState(
                    (refresh.error.message ?: stringResource(R.string.generic_error)),
                    onRetry = { items.retry() },
                )
                else -> if (items.itemCount == 0) {
                    EmptyState(stringResource(R.string.customers_search_hint))
                } else {
                    LazyColumn(
                        contentPadding = screenContentPadding(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(items.itemCount) { index ->
                            val customer = items[index] ?: return@items
                            CustomerRow(
                                customer = customer,
                                onEdit = { addOrEdit = EditTarget(customer) },
                                onCreateLogin = { loginTarget = customer },
                                onResetPassword = { resetTarget = customer },
                                onReactivate = { viewModel.reactivate(customer.id) { handle(it) } },
                                onStop = { viewModel.stop(customer.id) { handle(it) } },
                                onDelete = { viewModel.deletePermanent(customer.id) { handle(it) } },
                            )
                        }
                    }
                }
            }
        }
    }

    addOrEdit?.let { target ->
        CustomerFormDialog(
            existing = target.customer,
            onDismiss = { addOrEdit = null },
            onSave = { req ->
                viewModel.save(target.customer?.id, req) { result ->
                    handle(result)
                    if (result.isSuccess) addOrEdit = null
                }
            },
        )
    }
    loginTarget?.let { c ->
        CreateLoginDialog(
            customer = c,
            onDismiss = { loginTarget = null },
            onSubmit = { user, pass ->
                viewModel.createLogin(c.id, user, pass) { result ->
                    handle(result)
                    if (result.isSuccess) loginTarget = null
                }
            },
        )
    }
    resetTarget?.let { c ->
        ResetPasswordDialog(
            name = c.name,
            onDismiss = { resetTarget = null },
            onSubmit = { pass ->
                val userId = c.userId
                if (userId == null) {
                    resetTarget = null
                } else {
                    viewModel.resetPassword(userId, pass) { result ->
                        handle(result)
                        if (result.isSuccess) resetTarget = null
                    }
                }
            },
        )
    }
}

private data class EditTarget(val customer: CustomerResponse?)

@Composable
private fun CustomerRow(
    customer: CustomerResponse,
    onEdit: () -> Unit,
    onCreateLogin: () -> Unit,
    onResetPassword: () -> Unit,
    onReactivate: () -> Unit,
    onStop: () -> Unit,
    onDelete: () -> Unit,
) {
    var menu by remember { mutableStateOf(false) }
    OutlinedCard(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    customer.name,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    customer.phone,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                customer.username?.let {
                    Text(
                        "@$it",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                customer.customMilkRate?.let {
                    Text(formatMoney(it), style = MaterialTheme.typography.bodySmall)
                }
            }
            StatusBadge(customer.status.name, statusColor(customer.status.name))
            Box {
                IconButton(onClick = { menu = true }) { Icon(Icons.Filled.MoreVert, contentDescription = null) }
                DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                    DropdownMenuItem(text = { Text(stringResource(R.string.action_edit)) }, onClick = { menu = false; onEdit() })
                    if (customer.userId == null) {
                        DropdownMenuItem(text = { Text(stringResource(R.string.customer_create_login)) }, onClick = { menu = false; onCreateLogin() })
                    } else {
                        DropdownMenuItem(text = { Text(stringResource(R.string.customer_reset_password)) }, onClick = { menu = false; onResetPassword() })
                    }
                    if (customer.status == CustomerStatus.INACTIVE) {
                        DropdownMenuItem(text = { Text(stringResource(R.string.customer_reactivate)) }, onClick = { menu = false; onReactivate() })
                        DropdownMenuItem(text = { Text(stringResource(R.string.customer_delete)) }, onClick = { menu = false; onDelete() })
                    } else {
                        DropdownMenuItem(text = { Text(stringResource(R.string.customer_stop)) }, onClick = { menu = false; onStop() })
                    }
                }
            }
        }
    }
}
