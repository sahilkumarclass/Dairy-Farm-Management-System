package com.sahilkumar.dfms.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sahilkumar.dfms.R
import com.sahilkumar.dfms.core.ui.components.ErrorState
import com.sahilkumar.dfms.core.ui.components.LoadingState
import com.sahilkumar.dfms.core.util.formatLiters
import com.sahilkumar.dfms.core.util.formatMoney
import com.sahilkumar.dfms.model.DashboardSummary

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    when {
        state.loading -> LoadingState()
        state.error != null -> ErrorState(state.error!!, onRetry = viewModel::load)
        state.data != null -> DashboardContent(state.data!!)
    }
}

private data class Kpi(val label: String, val value: String)

@Composable
private fun DashboardContent(data: DashboardSummary) {
    val kpis = listOf(
        Kpi(stringResource(R.string.dashboard_today_liters), formatLiters(data.todayLiters)),
        Kpi(stringResource(R.string.dashboard_today_sales), formatMoney(data.todaySales)),
        Kpi(stringResource(R.string.dashboard_month_liters), formatLiters(data.monthLiters)),
        Kpi(stringResource(R.string.dashboard_month_sales), formatMoney(data.monthSales)),
        Kpi(stringResource(R.string.dashboard_month_expenses), formatMoney(data.monthExpenses)),
        Kpi(stringResource(R.string.dashboard_month_profit), formatMoney(data.monthProfit)),
        Kpi(stringResource(R.string.dashboard_outstanding), formatMoney(data.outstandingDues)),
        Kpi(stringResource(R.string.dashboard_active_customers), data.activeCustomers.toString()),
    )
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(kpis) { kpi ->
            OutlinedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        kpi.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        kpi.value,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
            }
        }
    }
}
