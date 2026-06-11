package com.sahilkumar.dfms.feature.herd

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.sahilkumar.dfms.core.ui.components.AppSnackbarHost
import com.sahilkumar.dfms.core.ui.components.ErrorState
import com.sahilkumar.dfms.core.ui.components.LabeledValue
import com.sahilkumar.dfms.core.ui.components.LoadingState
import com.sahilkumar.dfms.core.ui.components.showError
import com.sahilkumar.dfms.core.util.formatDate
import com.sahilkumar.dfms.core.util.formatLiters
import com.sahilkumar.dfms.core.util.formatMoney
import com.sahilkumar.dfms.core.util.userMessage
import kotlinx.coroutines.launch

@Composable
fun CowDetailScreen(
    cowId: String,
    viewModel: CowDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showProduction by remember { mutableStateOf(false) }
    var showHealth by remember { mutableStateOf(false) }

    LaunchedEffect(cowId) { viewModel.load(cowId) }

    fun handle(result: Result<Unit>) {
        result.onFailure { e -> scope.launch { snackbar.showError(e.userMessage()) } }
    }

    Scaffold(snackbarHost = { AppSnackbarHost(snackbar) }) { padding ->
        when {
            state.loading -> LoadingState(Modifier.padding(padding))
            state.error != null -> ErrorState(state.error!!, onRetry = { viewModel.load(cowId) }, modifier = Modifier.padding(padding))
            state.detail != null -> {
                val detail = state.detail!!
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        OutlinedCard(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Text(detail.cow.tagNo, style = MaterialTheme.typography.titleLarge)
                                detail.cow.name?.let { Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                                LabeledValue(stringResource(R.string.cow_month_litres), formatLiters(detail.monthLiters))
                                LabeledValue(stringResource(R.string.cow_lifetime_litres), formatLiters(detail.lifetimeLiters))
                                LabeledValue(stringResource(R.string.cow_month_health_cost), formatMoney(detail.monthHealthCost))
                            }
                        }
                    }

                    item {
                        SectionHeader(stringResource(R.string.cow_production), stringResource(R.string.cow_add_production)) { showProduction = true }
                    }
                    items(count = state.productions.size, key = { state.productions[it].id }) { i ->
                        val p = state.productions[i]
                        OutlinedCard(Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text("${formatDate(p.productionDate)} · ${p.session.name}", style = MaterialTheme.typography.bodyMedium)
                                    Text(formatLiters(p.liters), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = { viewModel.deleteProduction(cowId, p.id) { handle(it) } }) {
                                    Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_delete))
                                }
                            }
                        }
                    }

                    item {
                        SectionHeader(stringResource(R.string.cow_health_logs), stringResource(R.string.cow_add_health)) { showHealth = true }
                    }
                    items(count = state.healthLogs.size, key = { state.healthLogs[it].id }) { i ->
                        val h = state.healthLogs[i]
                        OutlinedCard(Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text("${h.eventType.name} · ${formatDate(h.eventDate)}", style = MaterialTheme.typography.bodyMedium)
                                    val sub = listOfNotNull(
                                        h.vetName?.takeIf { it.isNotBlank() },
                                        h.cost?.let { formatMoney(it) },
                                        h.nextDueDate?.let { "→ ${formatDate(it)}" },
                                    ).joinToString(" · ")
                                    if (sub.isNotBlank()) {
                                        Text(sub, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                IconButton(onClick = { viewModel.deleteHealth(cowId, h.id) { handle(it) } }) {
                                    Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_delete))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showProduction) {
        ProductionDialog(
            cowId = cowId,
            onDismiss = { showProduction = false },
            onSave = { req -> viewModel.addProduction(cowId, req) { r -> handle(r); if (r.isSuccess) showProduction = false } },
        )
    }
    if (showHealth) {
        HealthLogDialog(
            cowId = cowId,
            onDismiss = { showHealth = false },
            onSave = { req -> viewModel.addHealth(cowId, req) { r -> handle(r); if (r.isSuccess) showHealth = false } },
        )
    }
}

@Composable
private fun SectionHeader(title: String, actionLabel: String, onAction: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        TextButton(onClick = onAction) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Text(actionLabel)
        }
    }
}
